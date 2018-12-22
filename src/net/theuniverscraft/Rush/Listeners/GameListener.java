
package net.theuniverscraft.Rush.Listeners;

import java.util.LinkedList;
import java.util.List;

import net.theuniverscraft.Rush.Rush;
import net.theuniverscraft.Rush.Enum.GameState;
import net.theuniverscraft.Rush.Managers.PlayersManager;
import net.theuniverscraft.Rush.Managers.RespawnManager;
import net.theuniverscraft.Rush.Managers.TeamManager;
import net.theuniverscraft.Rush.Utils.Translation;
import net.theuniverscraft.Rush.Utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GameListener
implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (Rush.getInstance().getGameState() != GameState.GAME) {
            return;
        }
        Player dead = event.getEntity();
        PlayersManager.TucPlayer tdead = PlayersManager.getInstance().getPlayer(dead);
        Player killer = dead.getKiller();
        if (killer != null) {
            Rush.getGameAPI().kill(killer);
            killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 0));
        }
        if (!tdead.getTeam().haveBed()) {
            tdead.setSpectator();
            dead.sendMessage(Translation.YOU_ARE_DEFINITIVLY_DEAD);
            this.onPlayerQuitGame(dead);
        }
        event.setDeathMessage(" " + ChatColor.YELLOW + dead.getName() + ChatColor.GRAY + " " + ChatColor.GRAY + (dead.getKiller() == null ? "a succombé." : new StringBuilder("a été tué par ").append(ChatColor.YELLOW).append(dead.getKiller().getName()).toString()));

    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    	
    	if (Rush.getInstance().getGameState() != GameState.GAME) {
            return;
        }
        final Player player = event.getPlayer();
        PlayersManager.TucPlayer tplayer = PlayersManager.getInstance().getPlayer(player);
        event.setRespawnLocation(tplayer.getTeam().getSpawnPlayer());
        if(tplayer.isSpectator()){
            PotionEffect  invc = new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 100);
            player.addPotionEffect(invc);
        }
        if (!tplayer.isSpectator()) {
            RespawnManager.getInstance().addRespawner(tplayer);

        } else {
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Rush.getInstance(), new Runnable(){

                @Override
                public void run() {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
                }
            }, 10);
        }
        Utils.setInventory(player);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (Rush.getInstance().getGameState() != GameState.GAME) {
            return;
        }
        for (Block block : event.blockList()) {
            if (block.getType() != Material.BED_BLOCK) continue;
            for (TeamManager.TucTeam team : TeamManager.getInstance().getTeams()) {
                if (!team.isBed(block.getLocation())) continue;
                PlayersManager.TucPlayer tplayer = null;
                TeamManager.TucTeam enemy = null;
                if (event.getEntity().hasMetadata("player")) {
                    tplayer = PlayersManager.getInstance().getPlayer((Player)Utils.getMetadata((Metadatable)event.getEntity(), "player"));
                    enemy = tplayer.getTeam();
                }
                team.destroyBed(enemy);
                tplayer.getPlayer().setMaxHealth(tplayer.getPlayer().getMaxHealth() + 2.0);
                return;
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Rush.getInstance().getGameState() != GameState.GAME) {
            return;
        }
        if (event.getBlock().getType() == Material.BED_BLOCK) {
            event.setCancelled(true);
            return;
        }
        Player player = event.getPlayer();
        PlayersManager.TucPlayer tplayer = PlayersManager.getInstance().getPlayer(player);
        if (event.getBlock().getType() == Material.TNT && tplayer.getTeam().getBedLocation().distance(event.getBlock().getLocation()) <= 6.0) {
            player.sendMessage(Translation.YOU_CANNOT_PLACE_TNT);
            event.setCancelled(true);
            return;
        }
        
        
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Rush.getInstance().getGameState() != GameState.GAME) {
            return;
        }
        if (event.getBlock().getType() == Material.BED_BLOCK) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (Rush.getInstance().getGameState() != GameState.GAME) {
            return;
        }
        final Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getItemInHand().getType() == Material.FLINT_AND_STEEL && event.getClickedBlock().getType() == Material.TNT) {
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Rush.getInstance(), new Runnable(){

                @Override
                public void run() {
                    for (Entity entity : player.getNearbyEntities(3.0, 3.0, 3.0)) {
                        if (entity.getType() != EntityType.PRIMED_TNT) continue;
                        TNTPrimed tnt = (TNTPrimed)entity;
                        tnt.setMetadata("player", (MetadataValue)new FixedMetadataValue((Plugin)Rush.getInstance(), (Object)player));
                    }
                }
            }, 2);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.onPlayerQuitGame(event.getPlayer());
    }

    public void onPlayerQuitGame(Player player) {
        if (Rush.getInstance().getGameState() != GameState.GAME) {
            return;
        }
        Utils.updateSpectators();
        List<PlayersManager.TucPlayer> dead = PlayersManager.getInstance().getPlayersNotDead();
        if (dead.size() <= 1) {
            if (dead.size() == 1) {
                if (Rush.getInstance().setGameState(GameState.END_GAME)) {
                    TeamManager.TucTeam winner = dead.get(0).getTeam();
                    LinkedList<Player> players = new LinkedList<Player>();
                    for (PlayersManager.TucPlayer tplayer : winner.getPlayers()) {
                        players.add(tplayer.getPlayer());
                    }
                    Rush.getGameAPI().win(players);
                    Translation.broadcastWinner(winner);
                }
            } else if (Rush.getInstance().setGameState(GameState.END_GAME)) {
                Translation.broadcastWinner(null);
            }
            return;
        }
        int teamAlive = 0;
        for (TeamManager.TucTeam team : TeamManager.getInstance().getTeams()) {
            if (team.isDead()) continue;
            ++teamAlive;
        }
        if (teamAlive <= 1) {
            TeamManager.TucTeam winner = null;
            for (TeamManager.TucTeam team2 : TeamManager.getInstance().getTeams()) {
                if (team2.isDead()) continue;
                winner = team2;
            }
            if (Rush.getInstance().setGameState(GameState.END_GAME)) {
                Translation.broadcastWinner(winner);
                LinkedList<Player> players = new LinkedList<Player>();
                for (PlayersManager.TucPlayer tplayer : winner.getPlayers()) {
                    players.add(tplayer.getPlayer());
                }
                Rush.getGameAPI().win(players);
            }
        }
    }

}

