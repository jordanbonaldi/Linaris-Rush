
package net.theuniverscraft.Rush.Listeners;

import net.theuniverscraft.Rush.Rush;
import net.theuniverscraft.Rush.Enum.GameState;
import net.theuniverscraft.Rush.Managers.PlayersManager;
import net.theuniverscraft.Rush.Utils.Translation;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class GeneralListener
implements Listener {
    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        
		if(player.getName().equals("Neferett")) {
			event.setFormat(" §f§l[§c§lFondateur§f§l] §b" + player.getName()+"§f: " + event.getMessage());
		}
		else if(player.hasPermission("game.megavip")) {
			event.setFormat(" §f[§aMegaVip§f] §b" + player.getName()+"§f: " + event.getMessage());
		}else if(player.hasPermission("game.vip")){
			event.setFormat(" §f[§eVip§f] §b" + player.getName()+"§f: " + event.getMessage());
			
		}else if(player.hasPermission("game.modo")){
			event.setFormat(" §f[§6Modo§f] §b" + player.getName()+"§f: " + event.getMessage());
			
		}else if(player.hasPermission("game.admin")){
			event.setFormat(" §f[§cAdmin§f] §b" + player.getName()+"§f: " + event.getMessage());
		}else if(player.hasPermission("game.vipelite")){

			event.setFormat(" §f[§bVipElite§f] §b" + player.getName()+"§f: " + event.getMessage());
		}else{
			event.setFormat("§7" + player.getName()+" §f: "+ event.getMessage());	
		}
		
        final Player player1 = event.getPlayer();
        final PlayersManager.TucPlayer tplayer = PlayersManager.getInstance().getPlayer(player1);
        if(tplayer.haveTeam()){
       

		if(player1.getName().equals("Neferett")){
			event.setFormat("[" + tplayer.getTeam().getNameColored() + ChatColor.RESET + "]"+ " §f§l[§c§lFondateur§f§l] §b" + player1.getName()+"§f: " + event.getMessage());
		}
		else if(player1.hasPermission("game.megavip")) {
			event.setFormat("[" + tplayer.getTeam().getNameColored() + ChatColor.RESET + "]"+ " §f[§aMegaVip§f] §b" + player1.getName()+"§f: " + event.getMessage());
		}else if(player1.hasPermission("game.vip")){
			event.setFormat("[" + tplayer.getTeam().getNameColored() + ChatColor.RESET + "]"+ " §f[§eVip§f] §b" + player1.getName()+"§f: " + event.getMessage());
			
		}else if(player1.hasPermission("game.modo")){
			event.setFormat("[" + tplayer.getTeam().getNameColored() + ChatColor.RESET + "]"+ " §f[§6Modo§f] §b" + player1.getName()+"§f: " + event.getMessage());
			
		}else if(player1.hasPermission("game.admin")){
			event.setFormat("[" + tplayer.getTeam().getNameColored() + ChatColor.RESET + "]"+ " §f[§cAdmin§f] §b" + player1.getName()+"§f: " + event.getMessage());
		}else if(player1.hasPermission("game.vipelite")) {
			event.setFormat("[" + tplayer.getTeam().getNameColored() + ChatColor.RESET + "]"+ " §f[§bVipElite§f] §b" + player1.getName()+"§f: " + event.getMessage());
		}else{
			event.setFormat("[" + tplayer.getTeam().getNameColored() + ChatColor.RESET + "] " + player1.getName()+" §f: "+ event.getMessage());	
		}
		
        }
    }
    
    @EventHandler
    public void onServerPing(final ServerListPingEvent event) {
        String motd = null;
        final GameState state = Rush.getInstance().getGameState();
        if (state == GameState.BEFORE_GAME) {
            motd = Translation.MOTD_BEGIN_IN.replaceAll("<sec>", Long.toString(Rush.getInstance().getTimer().getTime()));
        }
        else if (state == GameState.GAME) {
            motd = "§cEn jeu";
        }
        else if (state == GameState.END_GAME || state == GameState.RESET) {
            motd = Translation.MOTD_END_GAME;
        }
        if (motd != null) {
            event.setMotd(motd);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(Translation.JOIN_MESSAGE.replaceAll("<player>", event.getPlayer().getName()));
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        event.setLeaveMessage(Translation.QUIT_MESSAGE.replaceAll("<player>", event.getPlayer().getName()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(Translation.QUIT_MESSAGE.replaceAll("<player>", event.getPlayer().getName()));
    }
}

