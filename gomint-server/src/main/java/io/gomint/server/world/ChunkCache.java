/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.world;

import io.gomint.math.MathUtils;
import io.gomint.server.SelfInstrumentation;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.util.Values;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class ChunkCache {

    private static final Logger LOGGER = LoggerFactory.getLogger( ChunkCache.class );

    // CHECKSTYLE:OFF
    // ==================================== FIELDS ==================================== //
    private final WorldAdapter world;
    private final Long2ObjectMap<ChunkAdapter> cachedChunks;
    private boolean enableAutoSave;
    private long autoSaveInterval;
    private long lastSaveCheck;

    // Ticking helper
    private float lastFullTickDT = 0;
    private LongSet alreadyTicked = new LongOpenHashSet();
    private final LongSet[] tempHashes = { new LongOpenHashSet(), new LongOpenHashSet() }; // 0 => not to delete chunks, 1 => to delete chunks

    public ChunkCache( WorldAdapter world ) {
        this.world = world;
        this.cachedChunks = new Long2ObjectOpenHashMap<>();
        this.enableAutoSave = world.getConfig().isAutoSave();
        this.autoSaveInterval = world.getConfig().getAutoSaveInterval();
    }
    // CHECKSTYLE:ON

    /**
     * Ticking for Chunk GC
     *
     * @param currentTimeMS The current time in milliseconds. Used to reduce the number of calls to System#currentTimeMillis()
     */
    public void tick( long currentTimeMS ) {
        // Check for gc
        int spawnXChunk = CoordinateUtils.fromBlockToChunk( (int) this.world.getSpawnLocation().getX() );
        int spawnZChunk = CoordinateUtils.fromBlockToChunk( (int) this.world.getSpawnLocation().getZ() );

        int spawnAreaSize = this.world.getConfig().getAmountOfChunksForSpawnArea();

        // Clear temp sets
        this.tempHashes[0].clear();
        this.tempHashes[1].clear();

        // Copy over the current loaded chunk hashes
        synchronized ( this ) {
            for ( EntityPlayer player : this.world.getPlayers0().keySet() ) {
                LongIterator chunkIterator = player.getConnection().getPlayerChunks().iterator();
                while ( chunkIterator.hasNext() ) {
                    this.tempHashes[0].add( chunkIterator.nextLong() );
                }

                chunkIterator = player.getConnection().getLoadingChunks().iterator();
                while ( chunkIterator.hasNext() ) {
                    this.tempHashes[0].add( chunkIterator.nextLong() );
                }
            }

            boolean checkChunkSave = this.isAutosaveEnabled() &&
                this.autoSaveInterval > 0 &&
                currentTimeMS - this.lastSaveCheck > 500;

            if ( checkChunkSave ) {
                this.lastSaveCheck = currentTimeMS;
            }

            for ( long l : this.cachedChunks.keySet() ) {
                ChunkAdapter chunk = null;
                if ( checkChunkSave ) {
                    chunk = this.cachedChunks.get( l );

                    if ( currentTimeMS - chunk.getLastSavedTimestamp() >= this.autoSaveInterval ) {
                        chunk.setLastSavedTimestamp( currentTimeMS );
                        this.world.saveChunkAsynchronously( chunk );
                    }
                }

                int currentX = (int) ( l >> 32 );
                int currentZ = (int) ( l ) + Integer.MIN_VALUE;

                // Check if this is part of the spawn
                if ( spawnAreaSize > 0 &&
                    currentX >= spawnXChunk - spawnAreaSize && currentX <= spawnXChunk + spawnAreaSize &&
                    currentZ >= spawnZChunk - spawnAreaSize && currentZ <= spawnZChunk + spawnAreaSize ) {
                    continue;
                }

                // Calculate the hashes which are used by players view distances
                if ( this.tempHashes[0].contains( l ) ) {
                    continue;
                }

                if ( chunk == null ) {
                    chunk = this.cachedChunks.get( l );
                }

                // Ask this chunk if he wants to be gced
                if ( !chunk.canBeGCed( currentTimeMS ) ) {
                    continue;
                }

                LOGGER.info( "Cleaning up chunk @ {} {}", currentX, currentZ );

                // Ask this chunk if he wants to be gced
                this.tempHashes[1].add( l );
            }

            if ( !this.tempHashes[1].isEmpty() ) {
                LongIterator toRemoveCursor = this.tempHashes[1].iterator();
                while ( toRemoveCursor.hasNext() ) {
                    this.cachedChunks.remove( toRemoveCursor.nextLong() );
                }
            }

            long size = SelfInstrumentation.getObjectSize( this.cachedChunks );
            if ( size > -1 ) {
                for ( Long2ObjectMap.Entry<ChunkAdapter> entry : this.cachedChunks.long2ObjectEntrySet() ) {
                    size += SelfInstrumentation.getObjectSize( entry );
                    size += SelfInstrumentation.getObjectSize( entry.getLongKey() );
                    size += SelfInstrumentation.getObjectSize( entry.getValue() );
                    size += entry.getValue().getMemorySize();
                }

                LOGGER.info( "Chunk cache size: {} bytes", size );
            }
        }
    }

    // ==================================== CHUNK CACHE ==================================== //

    /**
     * Attempts to get the specified chunk.
     *
     * @param x The x-coordinate of the chunk
     * @param z The z-coordinate of the chunk
     * @return The chunk if it is loaded or null otherwise
     */
    public ChunkAdapter getChunk( int x, int z ) {
        long chunkHash = CoordinateUtils.toLong( x, z );
        synchronized ( this ) {
            return this.getChunkInternal( chunkHash );
        }
    }

    /**
     * Puts the specified chunk into the cache thus making it available to the outside
     *
     * @param chunk The chunk to put into the cache
     */
    public void putChunk( ChunkAdapter chunk ) {
        long key = CoordinateUtils.toLong( chunk.getX(), chunk.getZ() );
        synchronized ( this ) {
            this.cachedChunks.put( key, chunk );
        }
    }

    // ==================================== AUTOSAVE ==================================== //

    /**
     * Checks whether or not the auto-save feature is enabled.
     *
     * @return Whether or not the auto-save feature is enabled
     */
    public boolean isAutosaveEnabled() {
        return this.enableAutoSave;
    }

    /**
     * Gets the interval in milliseconds in which chunks get saved automatically.
     *
     * @return The auto-save interval
     */
    public long getAutoSaveInterval() {
        return this.autoSaveInterval;
    }

    /**
     * Get a chunk based on his hash
     *
     * @param chunkHash which should be get
     * @return chunk adapter for the given hash or null when the hash has no chunk attached
     */
    ChunkAdapter getChunkInternal( long chunkHash ) {
        synchronized ( this ) {
            return this.cachedChunks.get( chunkHash );
        }
    }

    long[] getTickingChunks( float dT ) {
        synchronized ( this ) {
            this.lastFullTickDT += dT;
            if ( this.lastFullTickDT >= Values.CLIENT_TICK_RATE ) {
                // We need to tick all chunks which haven't been ticked until now
                long[] returnVal = new long[this.cachedChunks.size()];
                int index = 0;

                Long2ObjectMap.FastEntrySet<ChunkAdapter> set = (Long2ObjectMap.FastEntrySet<ChunkAdapter>) this.cachedChunks.long2ObjectEntrySet();
                ObjectIterator<Long2ObjectMap.Entry<ChunkAdapter>> iterator = set.fastIterator();
                while ( iterator.hasNext() ) {
                    long l = iterator.next().getLongKey();
                    if ( !this.alreadyTicked.contains( l ) ) {
                        returnVal[index++] = l;
                    }
                }

                this.lastFullTickDT = 0;
                this.alreadyTicked.clear();
                return Arrays.copyOf( returnVal, index );
            } else {
                // Check how many chunks we need to tick
                int max = this.cachedChunks.size();

                float currentTPS = 1 / dT;
                int needCurrent = MathUtils.fastFloor( max * ( 20F / currentTPS ) );

                // This only happens on first tick though
                if ( needCurrent == 0 ) {
                    return new long[0];
                }

                long[] returnVal = new long[needCurrent];
                int index = 0;

                Long2ObjectMap.FastEntrySet<ChunkAdapter> set = (Long2ObjectMap.FastEntrySet<ChunkAdapter>) this.cachedChunks.long2ObjectEntrySet();
                ObjectIterator<Long2ObjectMap.Entry<ChunkAdapter>> iterator = set.fastIterator();
                while ( iterator.hasNext() ) {
                    long l = iterator.next().getLongKey();
                    if ( !this.alreadyTicked.contains( l ) ) {
                        returnVal[index++] = l;
                        this.alreadyTicked.add( l );

                        if ( index == needCurrent ) {
                            break;
                        }
                    }
                }

                if ( index == needCurrent ) {
                    return returnVal;
                }

                return Arrays.copyOf( returnVal, index );
            }
        }
    }

    /**
     * Save all chunks and persist them to disk
     */
    void saveAll() {
        synchronized ( this ) {
            for ( long l : this.cachedChunks.keySet() ) {
                ChunkAdapter chunkAdapter = this.cachedChunks.get( l );
                this.world.saveChunk( chunkAdapter );
                chunkAdapter.setLastSavedTimestamp( this.world.getServer().getCurrentTickTime() );
            }
        }
    }

    public void iterateAll( Consumer<ChunkAdapter> chunkConsumer ) {
        synchronized ( this ) {
            for ( long l : this.cachedChunks.keySet() ) {
                chunkConsumer.accept( this.cachedChunks.get( l ) );
            }
        }
    }

}
