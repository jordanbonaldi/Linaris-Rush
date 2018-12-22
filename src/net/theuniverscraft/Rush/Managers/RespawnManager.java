
package net.theuniverscraft.Rush.Managers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.theuniverscraft.Rush.Rush;
import net.theuniverscraft.Rush.Utils.Translation;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RespawnManager {
    private List<Respawner> m_respawners = new ArrayList<Respawner>();
    private static RespawnManager instance;

    public static RespawnManager getInstance() {
        if (instance == null) {
            instance = new RespawnManager();
        }
        return instance;
    }

    private RespawnManager() {
    }

    public void addRespawner(final PlayersManager.TucPlayer player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Rush.getInstance(), new Runnable(){

            @Override
            public void run() {
                player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 8));
                player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0));
            }
        }, 5);
        this.m_respawners.add(new Respawner(this, player));
    }

    public void update() {
        int i = 0;
        while (i < this.m_respawners.size()) {
            if (this.m_respawners.get(i).update()) {
                this.m_respawners.remove(i);
                --i;
            }
            ++i;
        }
    }

    public boolean isRespawner(PlayersManager.TucPlayer player) {
        for (Respawner respawner : this.m_respawners) {
            if (!respawner.getUUID().equals(player.getUUID())) continue;
            return true;
        }
        return false;
    }

    public class Respawner {
        private PlayersManager.TucPlayer m_player;
        private long m_time;
        final  RespawnManager this$0;

        private Respawner(RespawnManager respawnManager, PlayersManager.TucPlayer player) {
            this.this$0 = respawnManager;
            this.m_time = 6;
            this.m_player = player;
        }

        private boolean update() {
            --this.m_time;
            if (this.m_time <= 0) {
                this.m_player.getPlayer().sendMessage(Translation.YOU_RESPAWN);
                return true;
            }
            if (this.m_time == 1) {
                this.m_player.getPlayer().sendMessage(Translation.YOU_RESPAWN_IN_ONE);
            } else {
                this.m_player.getPlayer().sendMessage(Translation.YOU_RESPAWN_IN.replaceAll("<sec>", Long.toString(this.m_time)));
            }
            return false;
        }

        public PlayersManager.TucPlayer getPlayer() {
            return this.m_player;
        }

        public UUID getUUID() {
            return this.m_player.getUUID();
        }

    }

}

