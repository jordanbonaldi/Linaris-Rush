
package net.theuniverscraft.Rush.Listeners;

import java.util.List;
import net.theuniverscraft.Rush.Enum.GameState;
import net.theuniverscraft.Rush.Enum.TeamColor;
import net.theuniverscraft.Rush.Managers.PlayersManager;
import net.theuniverscraft.Rush.Managers.TeamManager;
import net.theuniverscraft.Rush.Rush;
import net.theuniverscraft.Rush.Utils.Translation;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class ChooseTeamListener
implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (Rush.getInstance().getGameState() != GameState.BEFORE_GAME) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            try {
                Player player = event.getPlayer();
                PlayersManager.TucPlayer tplayer = PlayersManager.getInstance().getPlayer(player);
                ItemStack is = player.getItemInHand();
                if (is.getType() == Material.WOOL) {
                    DyeColor dyeColor = DyeColor.getByData((byte)((byte)is.getDurability()));
                    TeamColor color = TeamColor.getByeDyeColor(dyeColor);
                    TeamManager.TucTeam team = TeamManager.getInstance().getTeam(color);
                    List<TeamManager.TucTeam> teams = TeamManager.getInstance().getTeamNotFull();
                    if (teams.size() >= 2) {
                        int compare;
                        int team1Nb = teams.get(0).getPlayers().size();
                        int team2Nb = teams.get(1).getPlayers().size();
                        if (tplayer.haveTeam()) {
                            if (tplayer.getTeam().equals(teams.get(0))) {
                                --team1Nb;
                            } else if (tplayer.getTeam().equals(teams.get(1))) {
                                --team2Nb;
                            }
                        }
                        if ((compare = Integer.compare(team1Nb, team2Nb)) != 0 && (compare == 1 && team.equals(teams.get(0)) || compare == -1 && team.equals(teams.get(1)))) {
                            player.sendMessage(Translation.TEAM_IS_TOO_FULL);
                            return;
                        }
                    }
                    if (team.isFull()) {
                        player.sendMessage(Translation.TEAM_ARE_FULL.replaceAll("<team>", (Object)color.getChatColor() + color.toLocal()).replaceAll("<x>", Integer.toString(team.getPlayers().size())).replaceAll("<max>", Integer.toString(4)));
                    } else {
                        tplayer.setTeam(team);
                        player.sendMessage(Translation.YOU_ARE_IN_TEAM.replaceAll("<team>", (Object)color.getChatColor() + color.toLocal()).replaceAll("<x>", Integer.toString(team.getPlayers().size())).replaceAll("<max>", Integer.toString(4)));
                    }
                    event.setCancelled(true);
                    player.updateInventory();
                    return;
                }
                if (is.getType() == Material.NETHER_STAR) {
                    player.openInventory(Rush.getGameAPI().getKitInventory(player));
                }
            }
            catch (Exception player) {
                // empty catch block
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (Rush.getInstance().getGameState() == GameState.BEFORE_GAME) {
            Rush.getGameAPI().onInventoryClick(event);
        }
    }
}

