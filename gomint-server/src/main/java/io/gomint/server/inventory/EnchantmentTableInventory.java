package io.gomint.server.inventory;

import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.network.type.WindowType;

/**
 * @author geNAZt
 * @version 1.0
 */
public class EnchantmentTableInventory extends ContainerInventory {

    /**
     * Construct a new container inventory
     *
     * @param owner of the container (mostly a tile or normal entity)
     */
    public EnchantmentTableInventory( InventoryHolder owner ) {
        super( owner, 2 );
    }

    @Override
    public WindowType getType() {
        return WindowType.ENCHANTMENT;
    }

    @Override
    public void onOpen( EntityPlayer player ) {

    }

    @Override
    public void onClose( EntityPlayer player ) {

    }

}
