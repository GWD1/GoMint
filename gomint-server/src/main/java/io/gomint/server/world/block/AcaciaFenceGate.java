package io.gomint.server.world.block;

import io.gomint.inventory.item.*;
import io.gomint.server.registry.RegisterInfo;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 187 )
public class AcaciaFenceGate extends Block {

    @Override
    public int getBlockId() {
        return 187;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public Class<? extends ItemStack>[] getToolInterfaces() {
        return new Class[]{
            ItemWoodenAxe.class,
            ItemIronAxe.class,
            ItemDiamondAxe.class,
            ItemGoldenAxe.class,
            ItemStoneAxe.class
        };
    }

    @Override
    public long getBreakTime() {
        return 3000;
    }

    @Override
    public boolean canBeBrokenWithHand() {
        return true;
    }

}
