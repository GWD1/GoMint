package io.gomint.server.world.block;

import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 26 )
public class Bed extends Block {

    @Override
    public int getBlockId() {
        return 26;
    }

    @Override
    public long getBreakTime() {
        return 350;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean canBeBrokenWithHand() {
        return true;
    }

    @Override
    public boolean onBreak() {
        // There is another part we need to find
        return false;
    }

}
