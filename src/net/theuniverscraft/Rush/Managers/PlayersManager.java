
package net.theuniverscraft.Rush.Managers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayersManager {
    private List<TucPlayer> m_players = new ArrayList<TucPlayer>();
    private static PlayersManager instance;

    public static PlayersManager getInstance() {
        if (instance == null) {
            instance = new PlayersManager();
        }
        return instance;
    }

    private PlayersManager() {
    }

	public TucPlayer getPlayer(Player player) {
		for(TucPlayer tplayer : m_players) {
			if(tplayer.getUUID().equals(player.getUniqueId())) return tplayer;
		}
		
		TucPlayer tplayer = new TucPlayer(this,player);
		m_players.add(tplayer);
		return tplayer;
	}

    public void deletePlayer(Player player) {
        int i = 0;
        while (i < this.m_players.size()) {
            if (this.m_players.get(i).getUUID().equals(player.getUniqueId())) {
                this.m_players.remove(i);
                return;
            }
            ++i;
        }
    }

    public List<TucPlayer> getPlayersByTeam(TeamManager.TucTeam team) {
        ArrayList<TucPlayer> players = new ArrayList<TucPlayer>();
        for (TucPlayer player : this.m_players) {
            if (!player.haveTeam() || !player.getTeam().getColor().equals((Object)team.getColor())) continue;
            players.add(player);
        }
        return players;
    }

    public List<TucPlayer> getPlayersWithoutTeam() {
        ArrayList<TucPlayer> players = new ArrayList<TucPlayer>();
        for (TucPlayer player : this.m_players) {
            if (player.haveTeam()) continue;
            players.add(player);
        }
        return players;
    }

    public List<TucPlayer> getPlayersNotDead() {
        ArrayList<TucPlayer> players = new ArrayList<TucPlayer>();
        for (TucPlayer player : this.m_players) {
            if (player.isSpectator()) continue;
            players.add(player);
        }
        return players;
    }

    public List<TucPlayer> getSpectator() {
        ArrayList<TucPlayer> players = new ArrayList<TucPlayer>();
        for (TucPlayer player : this.m_players) {
            if (!player.isSpectator()) continue;
            players.add(player);
        }
        return players;
    }

    public List<TucPlayer> getPlayers() {
        return this.m_players;
    }

    public class TucPlayer {
        private Player m_player;
        private TeamManager.TucTeam m_team;
        private boolean m_spectator;
        final  PlayersManager this$0;

        private TucPlayer(PlayersManager playersManager, Player player) {
            this.this$0 = playersManager;
            this.m_spectator = false;
            this.m_player = player;
        }

        public boolean haveTeam() {
            if (this.m_team != null) {
                return true;
            }
            return false;
        }

        public void setTeam(TeamManager.TucTeam team) {
            this.m_team = team;
            this.m_team.addPlayer(this.m_player);
        }

        public TeamManager.TucTeam getTeam() {
            return this.m_team;
        }

        public Player getPlayer() {
            return this.m_player;
        }

        public UUID getUUID() {
            return this.m_player.getUniqueId();
        }

        public boolean isRespawner() {
            return RespawnManager.getInstance().isRespawner(this);
        }

        public void setSpectator() {
            this.m_spectator = true;
            this.m_player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
            this.m_team.removePlayer(this.m_player);
            this.m_team = null;
        }

        public boolean isSpectator() {
            return this.m_spectator;
        }

    }

}

