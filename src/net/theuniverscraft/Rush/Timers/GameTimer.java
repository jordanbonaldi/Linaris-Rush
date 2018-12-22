
package net.theuniverscraft.Rush.Timers;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import net.theuniverscraft.Rush.Enum.GameState;
import net.theuniverscraft.Rush.Enum.PopInfo;
import net.theuniverscraft.Rush.Managers.PlayersManager;
import net.theuniverscraft.Rush.Managers.RespawnManager;
import net.theuniverscraft.Rush.Managers.TeamManager;
import net.theuniverscraft.Rush.Rush;
import net.theuniverscraft.Rush.Utils.Translation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GameTimer
implements Runnable {
    private long m_time = 120;
    private HashMap<PopInfo, Long> m_pop = new HashMap();

    public GameTimer() {
        PopInfo[] arrpopInfo = PopInfo.values();
        int n = arrpopInfo.length;
        int n2 = 0;
        while (n2 < n) {
            PopInfo pop = arrpopInfo[n2];
            this.m_pop.put(pop, pop.getPop());
            ++n2;
        }
    }

    @Override
    public void run() {
        GameState state = Rush.getInstance().getGameState();
        if (state != GameState.BEFORE_GAME && state != GameState.GAME && state != GameState.END_GAME) {
            return;
        }
        --this.m_time;
        if (state == GameState.BEFORE_GAME) {
            if (this.m_time == 0) {
                Bukkit.broadcastMessage((String)Translation.THE_GAME_BEGIN);
            } else if (this.m_time == 1) {
                Bukkit.broadcastMessage((String)Translation.THE_GAME_BEGIN_IN_ONE);
            } else if (this.m_time % 10 == 0 || this.m_time <= 5) {
                Bukkit.broadcastMessage((String)Translation.THE_GAME_BEGIN_IN.replaceAll("<sec>", Long.toString(this.m_time)));
            }
            if (this.m_time == 2) {
                if (Bukkit.getOnlinePlayers().length <= 1) {
                    this.m_time = 120;
                    return;
                }
                List<PlayersManager.TucPlayer> withoutTeam = PlayersManager.getInstance().getPlayersWithoutTeam();
                int i = 0;
                while (i < withoutTeam.size()) {
                    withoutTeam.get(i).setTeam(TeamManager.getInstance().getTeamNotFull().get(0));
                    withoutTeam.remove(i);
                    --i;
                    ++i;
                }
            }
        } else if (state == GameState.GAME) {
            Player[] arrplayer = Bukkit.getOnlinePlayers();
            int n = arrplayer.length;
            int i = 0;
            while (i < n) {
                Player player = arrplayer[i];
                player.setLevel((int)this.m_time);
                ++i;
            }
            RespawnManager.getInstance().update();
            for (PopInfo pop : this.m_pop.keySet()) {
                Long time = this.m_pop.get((Object)pop);
                if ((time = Long.valueOf(time - 1)) <= 0) {
                    time = pop.getPop();
                    for (TeamManager.TucTeam team : TeamManager.getInstance().getTeams()) {
                        team.getSpawnItem().getWorld().dropItemNaturally(team.getSpawnItem(), pop.getIs());
                    }
                }
                this.m_pop.put(pop, time);
            }
        }
        if (this.m_time <= 0) {
            if (state == GameState.BEFORE_GAME) {
                if (Bukkit.getOnlinePlayers().length <= 1) {
                    this.m_time = 120;
                    return;
                }
                this.m_time = Long.MAX_VALUE;
                Rush.getInstance().setGameState(GameState.GAME);
            } else if (state == GameState.END_GAME) {
                Rush.getInstance().setGameState(GameState.RESET);
            }
        }
    }

    public void setTime(long time) {
        this.m_time = time;
    }

    public long getTime() {
        return this.m_time;
    }
}

