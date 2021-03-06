package io.gomint.server.world.block;

import io.gomint.math.AxisAlignedBB;
import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 158 )
public class WoodenSlab extends Slab {

    @Override
    public int getBlockId() {
        return 158;
    }

    @Override
    public long getBreakTime() {
        return 3000;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
