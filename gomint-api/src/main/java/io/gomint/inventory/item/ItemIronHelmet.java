package io.gomint.inventory.item;

import io.gomint.GoMint;

/**
 * @author geNAZt
 * @version 1.0
 */
public interface ItemIronHelmet extends ItemStack {

    /**
     * Create a new item stack with given class and amount
     *
     * @param amount which is used for the creation
     */
    static ItemIronHelmet create( int amount ) {
        return GoMint.instance().createItemStack( ItemIronHelmet.class, amount );
    }

}