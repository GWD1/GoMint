package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 228 )
public class ItemSilverGlazedTerracotta extends ItemStack {

    // CHECKSTYLE:OFF
    public ItemSilverGlazedTerracotta( short data, int amount ) {
        super( 228, data, amount );
    }

    public ItemSilverGlazedTerracotta( short data, int amount, NBTTagCompound nbt ) {
        super( 228, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
