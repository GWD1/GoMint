package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 226 )
public class ItemPinkGlazedTerracotta extends ItemStack {

    // CHECKSTYLE:OFF
    public ItemPinkGlazedTerracotta( short data, int amount ) {
        super( 226, data, amount );
    }

    public ItemPinkGlazedTerracotta( short data, int amount, NBTTagCompound nbt ) {
        super( 226, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
