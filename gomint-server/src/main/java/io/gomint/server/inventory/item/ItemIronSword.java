package io.gomint.server.inventory.item;

import io.gomint.inventory.item.ItemSword;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 267 )
 public class ItemIronSword extends ItemReduceTierIron implements io.gomint.inventory.item.ItemIronSword, ItemSword {

    // CHECKSTYLE:OFF
    public ItemIronSword( short data, int amount ) {
        super( 267, data, amount );
    }

    public ItemIronSword( short data, int amount, NBTTagCompound nbt ) {
        super( 267, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
