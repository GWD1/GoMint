package io.gomint.server.entity.tileentity;

import io.gomint.server.world.WorldAdapter;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 */
public class DaylightDetectorTileEntity extends TileEntity {

    /**
     * Construct new TileEntity from TagCompound
     *
     * @param tagCompound The TagCompound which should be used to read data from
     * @param world       The world in which this TileEntity resides
     */
    public DaylightDetectorTileEntity( NBTTagCompound tagCompound, WorldAdapter world ) {
        super( tagCompound, world );
    }

    @Override
    public void update( long currentMillis, float dF ) {

    }

    @Override
    public void toCompound( NBTTagCompound compound ) {
        super.toCompound( compound );

        compound.addValue( "id", "DaylightDetector" );
    }

}
