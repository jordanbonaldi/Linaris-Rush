
package net.theuniverscraft.Rush;

import net.theuniverscraft.Rush.Commands.CommandRush;
import net.theuniverscraft.Rush.Enum.GameState;
import net.theuniverscraft.Rush.Listeners.BasicListener;
import net.theuniverscraft.Rush.Listeners.ChooseTeamListener;
import net.theuniverscraft.Rush.Listeners.GameListener;
import net.theuniverscraft.Rush.Listeners.GeneralListener;
import net.theuniverscraft.Rush.Listeners.JoinListener;
import net.theuniverscraft.Rush.Listeners.RespawnerAndSpectatorListener;
import net.theuniverscraft.Rush.Managers.TeamManager;
import net.theuniverscraft.Rush.Timers.GameTimer;
import net.theuniverscraft.Rush.Utils.Constantes;
import net.theuniverscraft.Rush.Utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.skelerex.LinarisKits.api.GameAPI;
import com.skelerex.LinarisKits.api.GameType;

public class Rush
extends JavaPlugin {
    private Location m_lobby;
    private GameState m_gameState = GameState.BEFORE_GAME;
    private GameTimer m_timer = new GameTimer();
    private GameAPI m_gameAPI = new GameAPI(GameType.RUSHS);
    private static Rush instance;

    public static GameAPI getGameAPI() {
        return Rush.instance.m_gameAPI;
    }

    public static Rush getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.m_lobby = Utils.getLocation(this.getConfig().getConfigurationSection("lobby"));
        if (this.m_lobby == null) {
            this.m_lobby = ((World)Bukkit.getWorlds().get(0)).getSpawnLocation();
        }
        if (this.getConfig().contains("teams")) {
            Constantes.TEAM_NUMBER = this.getConfig().getInt("teams");
        }
        this.getCommand("rush").setExecutor((CommandExecutor)new CommandRush());
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents((Listener)new JoinListener(), (Plugin)this);
        pm.registerEvents((Listener)new ChooseTeamListener(), (Plugin)this);
        pm.registerEvents((Listener)new GameListener(), (Plugin)this);
        pm.registerEvents((Listener)new RespawnerAndSpectatorListener(), (Plugin)this);
        pm.registerEvents((Listener)new BasicListener(), (Plugin)this);
        pm.registerEvents((Listener)new GeneralListener(), (Plugin)this);
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)this.m_timer, 20, 20);
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getType() != EntityType.VILLAGER) continue;
                entity.remove();
            }
        }
        for (TeamManager.TucTeam team : TeamManager.getInstance().getTeams()) {
            if (team.isOk()) continue;
            this.m_gameState = GameState.INIT_GAME;
        }
    }

    public void setLobby(Location lobby) {
        this.m_lobby = lobby;
        this.m_lobby.getWorld().setSpawnLocation(this.m_lobby.getBlockX(), this.m_lobby.getBlockY(), this.m_lobby.getBlockZ());
        Utils.setLocation(this.getConfig().createSection("lobby"), this.m_lobby);
        this.saveConfig();
    }

    public boolean setGameState(GameState state) {
        if (this.m_gameState == state) {
            return false;
        }
        this.m_gameState = state;
        if (this.m_gameState == GameState.GAME) {
        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "time set 6000");
        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule doDaylightCycle false");
            for (TeamManager.TucTeam team : TeamManager.getInstance().getTeams()) {
                team.tpToSpawn();
            }
        } else if (this.m_gameState == GameState.END_GAME) {
            this.m_timer.setTime(8);
            Player[] arrplayer = Bukkit.getOnlinePlayers();
            int n = arrplayer.length;
            int n2 = 0;
            while (n2 < n) {
                Player player = arrplayer[n2];
                player.teleport(Rush.getInstance().getLobby());
                ++n2;
            }
        } else if (this.m_gameState == GameState.RESET) {
            Player[] arrplayer = Bukkit.getOnlinePlayers();
            int n = arrplayer.length;
            int n3 = 0;
            while (n3 < n) {
                Player player = arrplayer[n3];
                Utils.tpToLobby(player);
                ++n3;
            }
            for(Player p : Bukkit.getOnlinePlayers()){
            Utils.tpToLobby(p);
            }
            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)"wr reset");
            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)"stop");
        }
        return true;
    }

    public GameState getGameState() {
        return this.m_gameState;
    }

    public Location getLobby() {
        return this.m_lobby;
    }

    public GameTimer getTimer() {
        return this.m_timer;
    }
}

