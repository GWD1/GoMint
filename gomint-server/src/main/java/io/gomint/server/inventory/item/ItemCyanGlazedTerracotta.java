package io.gomint.server.inventory.item;

import io.gomint.server.registry.RegisterInfo;
import io.gomint.taglib.NBTTagCompound;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( id = 229 )
public class ItemCyanGlazedTerracotta extends ItemStack {

    // CHECKSTYLE:OFF
    public ItemCyanGlazedTerracotta( short data, int amount ) {
        super( 229, data, amount );
    }

    public ItemCyanGlazedTerracotta( short data, int amount, NBTTagCompound nbt ) {
        super( 229, data, amount, nbt );
    }
    // CHECKSTYLE:ON

}
