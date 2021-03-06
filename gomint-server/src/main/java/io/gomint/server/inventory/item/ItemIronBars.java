package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 101 )
 public class ItemIronBars extends ItemStack implements io.gomint.inventory.item.ItemIronBars {

    // CHECKSTYLE:OFF
    public ItemIronBars( short data, int amount ) {
        super( 101, data, amount );
    }

    public ItemIronBars( short data, int amount, NBTTagCompound nbt ) {
        super( 101, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
