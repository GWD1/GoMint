package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 397 )
public class ItemSkull extends ItemStack implements io.gomint.inventory.item.ItemSkull {

    // CHECKSTYLE:OFF
    public ItemSkull( short data, int amount ) {
        super( 397, data, amount );
    }

    public ItemSkull( short data, int amount, NBTTagCompound nbt ) {
        super( 397, data, amount, nbt );
    }
    // CHECKSTYLE:ON


    @Override
    public int getBlockId() {
        return 144;
    }

}
