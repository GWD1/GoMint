package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 461 )
public class ItemClownfish extends ItemFood implements io.gomint.inventory.item.ItemClownfish {

    // CHECKSTYLE:OFF
    public ItemClownfish( short data, int amount ) {
        super( 461, data, amount );
    }

    public ItemClownfish( short data, int amount, NBTTagCompound nbt ) {
        super( 461, data, amount, nbt );
    }
    // CHECKSTYLE:ON

    @Override
    public float getSaturation() {
        return 0.1f;
    }

    @Override
    public float getHunger() {
        return 1;
    }

}
