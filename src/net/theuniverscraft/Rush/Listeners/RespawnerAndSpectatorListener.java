
package net.theuniverscraft.Rush.Listeners;

import net.theuniverscraft.Rush.Rush;
import net.theuniverscraft.Rush.Enum.GameState;
import net.theuniverscraft.Rush.Managers.PlayersManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RespawnerAndSpectatorListener
implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (Rush.getInstance().getGameState() == GameState.INIT_GAME) {
            return;
        }
        Player player = event.getPlayer();
        PlayersManager.TucPlayer tplayer = PlayersManager.getInstance().getPlayer(player);
        if (tplayer.isRespawner() || tplayer.isSpectator()) {
            event.setCancelled(true);
        }
        if (tplayer.isSpectator()) {
            ItemMeta meta;
            if (event.getItem() == null) {
                return;
            }
            if (event.getItem().getType() == Material.SKULL_ITEM && (meta = event.getItem().getItemMeta()).hasDisplayName()) {
                String name = meta.getDisplayName();
                Player target = Bukkit.getPlayer((String)ChatColor.stripColor((String)name));
                if (target != null) {
                    player.teleport((Entity)target);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
                } else {
                    player.setItemInHand(null);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (Rush.getInstance().getGameState() == GameState.INIT_GAME) {
            return;
        }
        Player player = event.getPlayer();
        PlayersManager.TucPlayer tplayer = PlayersManager.getInstance().getPlayer(player);
        if (tplayer.isRespawner() || tplayer.isSpectator()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (Rush.getInstance().getGameState() == GameState.INIT_GAME) {
            return;
        }
        Player player = event.getPlayer();
        PlayersManager.TucPlayer tplayer = PlayersManager.getInstance().getPlayer(player);
        if (tplayer.isRespawner() || tplayer.isSpectator()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        if (Rush.getInstance().getGameState() == GameState.INIT_GAME) {
            return;
        }
        Player player = event.getPlayer();
        PlayersManager.TucPlayer tplayer = PlayersManager.getInstance().getPlayer(player);
        if (tplayer.isRespawner() || tplayer.isSpectator()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Rush.getInstance().getGameState() == GameState.INIT_GAME) {
            return;
        }
        Player player = event.getPlayer();
        PlayersManager.TucPlayer tplayer = PlayersManager.getInstance().getPlayer(player);
        if (tplayer.isRespawner() || tplayer.isSpectator()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Rush.getInstance().getGameState() == GameState.INIT_GAME) {
            return;
        }
        Player player = event.getPlayer();
        PlayersManager.TucPlayer tplayer = PlayersManager.getInstance().getPlayer(player);
        if (tplayer.isRespawner() || tplayer.isSpectator()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(PlayerItemConsumeEvent event) {
        if (Rush.getInstance().getGameState() == GameState.INIT_GAME) {
            return;
        }
        Player player = event.getPlayer();
        PlayersManager.TucPlayer tplayer = PlayersManager.getInstance().getPlayer(player);
        if (tplayer.isRespawner() || tplayer.isSpectator()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (Rush.getInstance().getGameState() == GameState.INIT_GAME) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getEntity();
        PlayersManager.TucPlayer tplayer = PlayersManager.getInstance().getPlayer(player);
        if (tplayer.isRespawner() || tplayer.isSpectator()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        PlayersManager.TucPlayer tplayer;
        if (event.getDamager() instanceof Player) {
            PlayersManager.TucPlayer tplayer2 = PlayersManager.getInstance().getPlayer((Player)event.getDamager());
            if (tplayer2.isRespawner() || tplayer2.isSpectator()) {
                event.setCancelled(true);
            }
        } else if (event.getEntity() instanceof Player && ((tplayer = PlayersManager.getInstance().getPlayer((Player)event.getEntity())).isRespawner() || tplayer.isSpectator())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            PlayersManager.TucPlayer tplayer = PlayersManager.getInstance().getPlayer(player);
            if (tplayer.isSpectator() || tplayer.isRespawner()) {
                event.setCancelled(true);
            }
        }
    }
}

