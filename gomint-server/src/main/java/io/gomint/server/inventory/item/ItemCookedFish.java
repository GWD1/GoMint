package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 350 )
public class ItemCookedFish extends ItemFood implements io.gomint.inventory.item.ItemCookedFish {

    // CHECKSTYLE:OFF
    public ItemCookedFish( short data, int amount ) {
        super( 350, data, amount );
    }

    public ItemCookedFish( short data, int amount, NBTTagCompound nbt ) {
        super( 350, data, amount, nbt );
    }
    // CHECKSTYLE:ON

    @Override
    public float getSaturation() {
        return 0.6f;
    }

    @Override
    public float getHunger() {
        return 5;
    }

}
