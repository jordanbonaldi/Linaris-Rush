
package net.theuniverscraft.Rush.Listeners;

import net.theuniverscraft.Rush.Rush;
import net.theuniverscraft.Rush.Enum.GameState;
import net.theuniverscraft.Rush.Managers.PlayersManager;
import net.theuniverscraft.Rush.Managers.TucScoreboardManager;
import net.theuniverscraft.Rush.Utils.Constantes;
import net.theuniverscraft.Rush.Utils.Translation;
import net.theuniverscraft.Rush.Utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class JoinListener
implements Listener {
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        GameState state = Rush.getInstance().getGameState();
        if (state != GameState.BEFORE_GAME) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Translation.GAME_ALREADY_START);
        } else if (Bukkit.getOnlinePlayers().length >= 4 * Constantes.TEAM_NUMBER) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Translation.GAME_FULL);
        } else if (Bukkit.getOnlinePlayers().length + 1 >= 4 * Constantes.TEAM_NUMBER) {
            Rush.getInstance().getTimer().setTime(21);
        }
        if (state == GameState.INIT_GAME && event.getPlayer().isOp()) {
            event.allow();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayersManager.getInstance().getPlayer(player);
        player.teleport(Rush.getInstance().getLobby());
        Utils.setInventory(player);
        player.setScoreboard(TucScoreboardManager.getScoreboard());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayersManager.getInstance().deletePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (Rush.getInstance().getGameState() != GameState.GAME) {
            event.setRespawnLocation(Rush.getInstance().getLobby());
        }
    }
}

