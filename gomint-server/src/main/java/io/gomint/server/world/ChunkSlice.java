package io.gomint.server.world;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.math.Location;
import io.gomint.math.MathUtils;
import io.gomint.server.SelfInstrumentation;
import io.gomint.server.entity.tileentity.TileEntity;
import io.gomint.server.util.Palette;
import io.gomint.server.util.collection.NumberArray;
import io.gomint.server.world.storage.TemporaryStorage;
import it.unimi.dsi.fastutil.longs.Long2IntArrayMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author geNAZt
 * @version 1.0
 */
@RequiredArgsConstructor
class ChunkSlice {

    @Getter
    private final ChunkAdapter chunk;
    @Getter
    private final int sectionY;

    private boolean isAllAir = true;

    private NumberArray blocks = null;
    private NibbleArray data = null;

    private NibbleArray blockLight = new NibbleArray( (short) 4096 );
    private NibbleArray skyLight = new NibbleArray( (short) 4096 );

    private Short2ObjectOpenHashMap<TileEntity> tileEntities = new Short2ObjectOpenHashMap<>();
    private Short2ObjectOpenHashMap<TemporaryStorage> temporaryStorages = new Short2ObjectOpenHashMap<>();

    private short getIndex( int x, int y, int z ) {
        return (short) ( ( x << 8 ) + ( z << 4 ) + y );
    }

    /**
     * Get the ID of the specific block in question
     *
     * @param x coordinate in this slice (capped to 16)
     * @param y coordinate in this slice (capped to 16)
     * @param z coordinate in this slice (capped to 16)
     * @return id of the block
     */
    int getBlock( int x, int y, int z ) {
        return this.getBlockInternal( getIndex( x, y, z ) );
    }

    /**
     * Get a block by its index
     *
     * @param index which should be looked up
     * @return block id of the index
     */
    int getBlockInternal( short index ) {
        if ( this.isAllAir ) {
            return 0;
        }

        return this.blocks.get( index );
    }

    <T extends io.gomint.world.block.Block> T getBlockInstance( int x, int y, int z ) {
        short index = getIndex( x, y, z );

        int fullX = CoordinateUtils.getChunkMin( this.chunk.getX() ) + x;
        int fullY = CoordinateUtils.getChunkMin( this.sectionY ) + y;
        int fullZ = CoordinateUtils.getChunkMin( this.chunk.getZ() ) + z;

        if ( this.isAllAir ) {
            return (T) this.chunk.getWorld().getServer().getBlocks().get( 0, (byte) 0, this.skyLight.get( index ), this.blockLight.get( index ), null, new Location( this.chunk.world, fullX, fullY, fullZ ) );
        }

        return (T) this.chunk.getWorld().getServer().getBlocks().get( this.blocks.get( index ), this.data == null ? 0 : this.data.get( index ), this.skyLight.get( index ),
            this.blockLight.get( index ), this.tileEntities.get( index ), new Location( this.chunk.world, fullX, fullY, fullZ ) );
    }

    Collection<TileEntity> getTileEntities() {
        return new ArrayList<>( this.tileEntities.values() );
    }

    void addTileEntity( int x, int y, int z, TileEntity tileEntity ) {
        this.tileEntities.put( getIndex( x, y, z ), tileEntity );
    }

    void setBlock( int x, int y, int z, int blockId ) {
        short index = getIndex( x, y, z );

        if ( blockId != 0 && this.blocks == null ) {
            this.blocks = new NumberArray();
            this.isAllAir = false;
        }

        if ( this.blocks != null ) {
            this.blocks.add( index, blockId );
        }
    }

    void setData( int x, int y, int z, byte data ) {
        short index = getIndex( x, y, z );

        // Check if we need to set new nibble array
        if ( !this.isAllAir && this.data == null ) {
            this.data = new NibbleArray( (short) 4096 );
        }

        // All air and we want to set block data? How about no!
        if ( this.data == null ) {
            return;
        }

        this.data.set( index, data );
    }

    byte getData( int x, int y, int z ) {
        if ( this.data == null ) {
            return 0;
        }

        return this.data.get( getIndex( x, y, z ) );
    }

    boolean isAllAir() {
        return this.isAllAir;
    }

    byte[] getBytes() {
        // Count how many unique blocks we have in this chunk
        int index = 0;
        Long2IntMap ids = new Long2IntArrayMap();
        Long2IntMap runtimeIndex = new Long2IntArrayMap();
        ids.defaultReturnValue( -1 );
        int[] indexIDs = new int[4096];

        for ( int x = 0; x < 16; x++ ) {
            for ( int z = 0; z < 16; z++ ) {
                for ( int y = 0; y < 16; y++ ) {
                    short blockIndex = (short) ( ( x << 8 ) + ( z << 4 ) + y );
                    int blockId = this.blocks == null ? 0 : this.blocks.get( blockIndex );
                    byte blockData = this.data == null ? 0 : this.data.get( blockIndex );

                    long hashId = ( (long) blockId ) << 32 | ( blockData & 0xffffffffL );
                    int foundIndex = ids.get( hashId );
                    if ( foundIndex == -1 ) {
                        int runtimeId = BlockRuntimeIDs.fromLegacy( blockId, blockData );
                        runtimeIndex.put( index, runtimeId );
                        ids.put( hashId, index );
                        foundIndex = index;
                        index++;
                    }

                    indexIDs[blockIndex] = foundIndex;
                }
            }
        }

        // Get correct wordsize
        int value = ids.size();
        int numberOfBits = MathUtils.fastFloor( log2( value ) ) + 1;

        // Prepare palette
        int amountOfBlocks = MathUtils.fastFloor( 32f / (float) numberOfBits );

        PacketBuffer buffer = new PacketBuffer( MathUtils.fastCeil( 4096 / (float) amountOfBlocks ) + 1 + 4 + ids.size() * 4 );
        Palette palette = new Palette( buffer, amountOfBlocks, false );

        byte paletteWord = (byte) ( (byte) ( palette.getPaletteVersion().getVersionId() << 1 ) | 1 );
        buffer.writeByte( paletteWord );

        for ( Integer id : indexIDs ) {
            palette.addIndex( id );
        }

        palette.finish();

        // Write runtimeIDs
        buffer.writeSignedVarInt( ids.size() );

        Long2IntMap.FastEntrySet entrySet = (Long2IntMap.FastEntrySet) runtimeIndex.long2IntEntrySet();
        ObjectIterator<Long2IntMap.Entry> iterator = entrySet.fastIterator();
        while ( iterator.hasNext() ) {
            buffer.writeSignedVarInt( iterator.next().getIntValue() );
        }

        // Copy result
        byte[] outputData = new byte[buffer.getPosition()];
        System.arraycopy( buffer.getBuffer(), buffer.getBufferOffset(), outputData, 0, buffer.getPosition() );
        return outputData;
    }

    private int log2( int n ) {
        if ( n <= 0 ) throw new IllegalArgumentException();
        return 31 - Integer.numberOfLeadingZeros( n );
    }

    public TemporaryStorage getTemporaryStorage( int x, int y, int z ) {
        short index = getIndex( x, y, z );

        TemporaryStorage storage = this.temporaryStorages.get( index );
        if ( storage == null ) {
            storage = new TemporaryStorage();
            this.temporaryStorages.put( index, storage );
        }

        return storage;
    }

    public void resetTemporaryStorage( int x, int y, int z ) {
        short index = getIndex( x, y, z );
        this.temporaryStorages.remove( index );
    }

    public long getMemorySize() {
        return this.isAllAir ? 0 : SelfInstrumentation.getObjectSize( this.blocks );
    }

}
