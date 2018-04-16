/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.plugin.listener;

import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import io.gomint.event.world.BlockBreakEvent;
import io.gomint.world.block.BlockType;

/**
 * @author geNAZt
 * @version 1.0
 */
public class BlockBreakListener implements EventListener {

    @EventHandler
    public void onBlockBreak( BlockBreakEvent event ) {
        /*if ( event.getBreakBlock().getType() == BlockType.BED ) {
            event.setCancelled( true );
        }*/
    }

}
