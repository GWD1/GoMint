package io.gomint.server.world.block;

import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 234 )
public class RedGlazedTerracotta extends Block {

    @Override
    public int getBlockId() {
        return 234;
    }

    @Override
    public long getBreakTime() {
        return 2100;
    }

}
