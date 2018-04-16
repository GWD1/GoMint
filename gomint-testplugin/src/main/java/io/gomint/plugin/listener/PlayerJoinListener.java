package io.gomint.plugin.listener;

import io.gomint.GoMint;
import io.gomint.entity.potion.PotionEffect;
import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import io.gomint.event.player.PlayerJoinEvent;
import io.gomint.inventory.item.*;
import io.gomint.math.BlockPosition;
import io.gomint.plugin.TestPlugin;
import io.gomint.world.Gamemode;
import io.gomint.world.block.Block;
import io.gomint.world.block.BlockStone;
import io.gomint.world.block.BlockType;
import io.gomint.world.block.BlockWood;
import io.gomint.world.block.data.BlockColor;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt
 * @version 1.0
 */
@RequiredArgsConstructor
public class PlayerJoinListener implements EventListener {

    private final TestPlugin plugin;

    @EventHandler
    public void onPlayerJoin( PlayerJoinEvent event ) {
        event.getPlayer().setDisplayName( "§6Project Lead §7|§6 " + event.getPlayer().getName() );
        event.getPlayer().teleport( GoMint.instance().getDefaultWorld().getSpawnLocation().clone().add( 0, 1, 0 ) );

        event.getPlayer().getInventory().setItem( 0, ItemArrow.create( 64 ) );
        event.getPlayer().getInventory().setItem( 1, ItemBow.create( 1 ) );
    }

}
