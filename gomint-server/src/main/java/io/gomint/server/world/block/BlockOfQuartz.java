package io.gomint.server.world.block;

import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 155 )
public class BlockOfQuartz extends Block {

    @Override
    public int getBlockId() {
        return 155;
    }

    @Override
    public long getBreakTime() {
        return 1200;
    }

}
