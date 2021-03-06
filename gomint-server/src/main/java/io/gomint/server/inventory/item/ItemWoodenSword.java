package io.gomint.server.inventory.item;

import io.gomint.inventory.item.ItemSword;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 268 )
 public class ItemWoodenSword extends ItemReduceTierWooden implements io.gomint.inventory.item.ItemWoodenSword, ItemSword {

    // CHECKSTYLE:OFF
    public ItemWoodenSword( short data, int amount ) {
        super( 268, data, amount );
    }

    public ItemWoodenSword( short data, int amount, NBTTagCompound nbt ) {
        super( 268, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
