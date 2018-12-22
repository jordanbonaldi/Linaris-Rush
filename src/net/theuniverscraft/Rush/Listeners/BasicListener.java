
package net.theuniverscraft.Rush.Listeners;

import net.theuniverscraft.Rush.Enum.GameState;
import net.theuniverscraft.Rush.Rush;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class BasicListener
implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        this.cancel((Cancellable)event);
        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                event.setCancelled(true);
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.BED_BLOCK) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        this.cancel((Cancellable)event);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        this.cancel((Cancellable)event);
    }

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        this.cancel((Cancellable)event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        this.cancel((Cancellable)event);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        this.cancel((Cancellable)event);
    }

    @EventHandler
    public void onBlockPlace(PlayerItemConsumeEvent event) {
        this.cancel((Cancellable)event);
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        this.cancel((Cancellable)event);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        this.cancel((Cancellable)event);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        this.cancel((Cancellable)event);
    }

    private void cancel(Cancellable event) {
        GameState state = Rush.getInstance().getGameState();
        if (state != GameState.GAME && state != GameState.INIT_GAME) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (Rush.getInstance().getGameState() == GameState.BEFORE_GAME && event.getTo().getY() < -15.0) {
            event.getPlayer().teleport(Rush.getInstance().getLobby());
        }
    }
}

