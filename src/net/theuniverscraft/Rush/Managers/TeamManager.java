
package net.theuniverscraft.Rush.Managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.theuniverscraft.Rush.Rush;
import net.theuniverscraft.Rush.Enum.TeamColor;
import net.theuniverscraft.Rush.Utils.Constantes;
import net.theuniverscraft.Rush.Utils.Translation;
import net.theuniverscraft.Rush.Utils.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class TeamManager {
    private List<TucTeam> m_teams = new ArrayList<TucTeam>();
    private static TeamManager instance;

    public static TeamManager getInstance() {
        if (instance == null) {
            instance = new TeamManager();
        }
        return instance;
    }

    private TeamManager() {
        try {
            int i = 0;
            while (i < Constantes.TEAM_NUMBER) {
                TeamColor color = TeamColor.values()[i];
                File dirs = new File("plugins/" + Rush.getInstance().getName() + "/teams");
                dirs.mkdirs();
                File teamFile = new File("plugins/" + Rush.getInstance().getName() + "/teams/" + color.toString() + ".yml");
                if (!teamFile.exists()) {
                    teamFile.createNewFile();
                }
                YamlConfiguration teamConfig = YamlConfiguration.loadConfiguration((File)teamFile);
                TucTeam team = new TucTeam(this, color, teamFile, (FileConfiguration)teamConfig);
                team.setSpawnPlayer(Utils.getLocation(teamConfig.getConfigurationSection("spawn_player")));
                team.setSpawnItem(Utils.getLocation(teamConfig.getConfigurationSection("spawn_item")));
                team.setBedLocation(Utils.getLocation(teamConfig.getConfigurationSection("bed_location")));
                this.m_teams.add(team);
                ++i;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean allTeamOk() {
        for (TucTeam team : this.m_teams) {
            if (team.isOk()) continue;
            return false;
        }
        return true;
    }

    public TucTeam getTeam(TeamColor color) {
        for (TucTeam team : this.m_teams) {
            if (!team.getColor().equals((Object)color)) continue;
            return team;
        }
        return null;
    }

    public List<TucTeam> getTeamNotFull() {
        ArrayList<TucTeam> teams = new ArrayList<TucTeam>();
        for (TucTeam team : this.m_teams) {
            if (team.isFull()) continue;
            teams.add(team);
        }
        Utils.sortList(teams, new Comparator<TucTeam>(){

            @Override
            public int compare(TucTeam team1, TucTeam team2) {
                return Integer.compare(team1.getPlayers().size(), team2.getPlayers().size());
            }
        });
        return teams;
    }

    public List<TucTeam> getTeams() {
        return this.m_teams;
    }

    public class TucTeam {
        private TeamColor m_color;
        private File m_file;
        private FileConfiguration m_config;
        private Location m_spawnPlayer;
        private Location m_spawnItem;
        private Location m_bed;
        private Team m_bukkitTeam;
        private int m_bedBreak;
        final  TeamManager this$0;

        private TucTeam(TeamManager teamManager, TeamColor color, File file, FileConfiguration config) {
            this.this$0 = teamManager;
            this.m_bedBreak = 0;
            this.m_color = color;
            this.m_file = file;
            this.m_config = config;
            this.m_bukkitTeam = TucScoreboardManager.getScoreboard().getTeam(this.m_color.toLocal()) != null ? TucScoreboardManager.getScoreboard().getTeam(this.m_color.toLocal()) : TucScoreboardManager.getScoreboard().registerNewTeam(this.m_color.toLocal());
            this.m_bukkitTeam.setAllowFriendlyFire(false);
            this.m_bukkitTeam.setDisplayName((Object)this.m_color.getChatColor() + this.m_color.toLocal());
            this.m_bukkitTeam.setPrefix((Object)ChatColor.RESET + "[" + (Object)this.m_color.getChatColor() + this.m_color.toLocal() + (Object)ChatColor.RESET + "] ");
        }

        public void tpToSpawn() {
            for (PlayersManager.TucPlayer player : this.getPlayers()) {
                player.getPlayer().teleport(this.m_spawnPlayer);
                Utils.setInventory(player.getPlayer());
            }
        }

        public boolean isBed(Location bed) {
            if (this.m_bed.getWorld().getUID().equals(bed.getWorld().getUID()) && Utils.plus1moins1equal(this.m_bed.getBlockX(), bed.getBlockX()) && Utils.plus1moins1equal(this.m_bed.getBlockY(), bed.getBlockY()) && Utils.plus1moins1equal(this.m_bed.getBlockZ(), bed.getBlockZ())) {
                return true;
            }
            return false;
        }

        public Location getBedLocation() {
            return this.m_bed;
        }

        public int getBedBreak() {
            return this.m_bedBreak;
        }

        public void destroyBed(TucTeam enemy) {
            if (enemy.getColor().equals((Object)this.m_color)) {
                enemy = null;
            }
            if (enemy != null) {
                ++enemy.m_bedBreak;
            }
            TucScoreboardManager.update();
            for (TucTeam team : TeamManager.getInstance().getTeams()) {
                if (team.getColor().equals((Object)enemy.getColor())) {
                    team.sendMessage(Translation.YOU_HAVE_DESTROY_BED.replaceAll("<team>", this.m_color.toLocal()));
                    continue;
                }
                if (team.getColor().equals((Object)this.m_color)) {
                    team.sendMessage(Translation.YOUR_BED_ARE_DESTROY.replaceAll("<team>", enemy.getColor().toLocal()));
                    continue;
                }
                team.sendMessage(Translation.A_BED_DESTROY.replaceAll("<enemy>", enemy.getColor().toLocal()).replaceAll("<victim>", this.m_color.toLocal()));
            }
        }

        public boolean haveBed() {
            if (this.m_bed.getBlock().getType() == Material.BED_BLOCK) {
                return true;
            }
            return false;
        }

        public void sendMessage(String msg) {
            for (PlayersManager.TucPlayer player : this.getPlayers()) {
                player.getPlayer().sendMessage(msg);
            }
        }

        public TeamColor getColor() {
            return this.m_color;
        }

        public Location getSpawnItem() {
            return this.m_spawnItem;
        }

        public void setSpawnPlayer(Location spawnPlayer) {
            this.m_spawnPlayer = spawnPlayer;
            Utils.setLocation(this.m_config.createSection("spawn_player"), this.m_spawnPlayer);
            this.save();
        }

        public void setSpawnItem(Location spawnItem) {
            this.m_spawnItem = spawnItem;
            Utils.setLocation(this.m_config.createSection("spawn_item"), this.m_spawnItem);
            this.save();
        }

        public void setBedLocation(Location bed) {
            this.m_bed = bed;
            Utils.setLocation(this.m_config.createSection("bed_location"), this.m_bed);
            this.save();
        }

        public List<PlayersManager.TucPlayer> getPlayers() {
            return PlayersManager.getInstance().getPlayersByTeam(this);
        }

        public boolean isFull() {
            if (this.getPlayers().size() >= 4) {
                return true;
            }
            return false;
        }

        public boolean isOk() {
            if (this.m_spawnPlayer != null && this.m_spawnItem != null && this.m_bed != null) {
                return true;
            }
            return false;
        }

        public boolean isDead() {
            for (PlayersManager.TucPlayer player : this.getPlayers()) {
                if (player.isSpectator()) continue;
                return false;
            }
            return true;
        }

        private void save() {
            try {
                this.m_config.save(this.m_file);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        public Location getSpawnPlayer() {
            return this.m_spawnPlayer;
        }

        public void kick(String msg) {
            for (PlayersManager.TucPlayer player : this.getPlayers()) {
                player.getPlayer().kickPlayer(msg);
            }
        }

        public void addPlayer(Player player) {
            this.m_bukkitTeam.addPlayer((OfflinePlayer)player);
        }

        public String getNameColored() {
            return (Object)this.m_color.getChatColor() + this.m_color.toLocal();
        }

        public void removePlayer(Player player) {
            this.m_bukkitTeam.removePlayer((OfflinePlayer)player);
        }

    }

}

