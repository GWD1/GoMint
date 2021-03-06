package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 463 )
public class ItemCookedSalmon extends ItemFood implements io.gomint.inventory.item.ItemCookedSalmon {

    // CHECKSTYLE:OFF
    public ItemCookedSalmon( short data, int amount ) {
        super( 463, data, amount );
    }

    public ItemCookedSalmon( short data, int amount, NBTTagCompound nbt ) {
        super( 463, data, amount, nbt );
    }
    // CHECKSTYLE:ON

    @Override
    public float getSaturation() {
        return 0.8f;
    }

    @Override
    public float getHunger() {
        return 6;
    }

}
