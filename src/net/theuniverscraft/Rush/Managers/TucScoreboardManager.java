
package net.theuniverscraft.Rush.Managers;

import java.util.List;
import net.theuniverscraft.Rush.Managers.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public abstract class TucScoreboardManager {
    private static Scoreboard m_scoreboard;
    private static Objective m_objective;

    public static Scoreboard getScoreboard() {
        TucScoreboardManager.init();
        return m_scoreboard;
    }

    public static void update() {
        TucScoreboardManager.init();
        for (TeamManager.TucTeam team : TeamManager.getInstance().getTeams()) {
            Score teamScore = m_objective.getScore(Bukkit.getOfflinePlayer((String)(String.valueOf(team.getNameColored()) + (Object)ChatColor.WHITE + ":")));
            teamScore.setScore(team.getBedBreak());
        }
    }

    private static void init() {
        if (m_scoreboard == null) {
            m_scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            m_objective = m_scoreboard.registerNewObjective("bed", "dummy");
            m_objective.setDisplayName((Object)ChatColor.GRAY + "- " + (Object)ChatColor.YELLOW + "Rush " + (Object)ChatColor.GRAY + "-");
            m_objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            TucScoreboardManager.update();
        }
    }
}

