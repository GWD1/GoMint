package io.gomint.server.world.block;

import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 186 )
public class DarkOakFenceGate extends Block {

    @Override
    public int getBlockId() {
        return 186;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
