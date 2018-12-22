
package net.theuniverscraft.Rush.Managers;

import java.util.LinkedList;
import java.util.List;

import net.theuniverscraft.Rush.Rush;
import net.theuniverscraft.Rush.Enum.TeamColor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class CreateTeamManager
implements Listener {
    private List<TeamCreator> m_clanCreators = new LinkedList<TeamCreator>();
    private static CreateTeamManager instance;

    public static CreateTeamManager getInstance() {
        if (instance == null) {
            instance = new CreateTeamManager();
        }
        return instance;
    }

    private CreateTeamManager() {
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)Rush.getInstance());
    }

    public void createTeam(Player player, TeamColor color) {
        this.m_clanCreators.add(new TeamCreator(this, player, color));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        int i = 0;
        while (i < this.m_clanCreators.size()) {
            this.m_clanCreators.get(i).onPlayerInteract(event);
            if (this.m_clanCreators.get(i).isFinish()) {
                this.m_clanCreators.remove(i);
                --i;
            }
            ++i;
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        int i = 0;
        while (i < this.m_clanCreators.size()) {
            this.m_clanCreators.get(i).onBlockPlace(event);
            if (this.m_clanCreators.get(i).isFinish()) {
                this.m_clanCreators.remove(i);
                --i;
            }
            ++i;
        }
    }

	public enum CreationEtapes {
		SPAWN_PLAYER(ChatColor.GREEN + "Définissez le spawn des joueurs (faîte un clique droit dans l'air)"),
		SPAWN_ITEM(ChatColor.GREEN + "Définissez le spawn des items (poser le bloc)"),
		BED_LOCATION(ChatColor.GREEN + "Poser le lit"),
		CREATION_END(ChatColor.DARK_GREEN + "La team est créé");
		
		private String m_msg;
		CreationEtapes(String msg) {
			m_msg = msg;
		}
		
		public String getMessage() { return m_msg; }
	}

    public class TeamCreator {
        private Player m_player;
        private CreationEtapes m_etape;
        private TeamColor m_color;
        private Location spawn_player;
        private Location spawn_item;
        private Location bed_location;
        final  CreateTeamManager this$0;

        private TeamCreator(CreateTeamManager createTeamManager, Player player, TeamColor color) {
            this.this$0 = createTeamManager;
            this.m_player = player;
            this.m_color = color;
            this.setEtape(CreationEtapes.SPAWN_PLAYER);
            this.m_player.setItemInHand(new ItemStack(Material.STICK, 1));
        }

        public void onPlayerInteract(PlayerInteractEvent event) {
            if (!this.m_player.getUniqueId().equals(event.getPlayer().getUniqueId())) {
                return;
            }
            if (this.m_etape == CreationEtapes.SPAWN_PLAYER && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                this.spawn_player = event.getPlayer().getLocation();
                this.m_player.setItemInHand(new ItemStack(Material.STONE));
                this.setEtape(CreationEtapes.SPAWN_ITEM);
            }
        }

        public void onBlockPlace(BlockPlaceEvent event) {
            if (!this.m_player.getUniqueId().equals(event.getPlayer().getUniqueId())) {
                return;
            }
            if (this.m_etape == CreationEtapes.SPAWN_ITEM) {
                this.spawn_item = event.getBlock().getLocation();
                this.m_player.setItemInHand(new ItemStack(Material.BED));
                this.setEtape(CreationEtapes.BED_LOCATION);
                event.setCancelled(true);
            } else if (this.m_etape == CreationEtapes.BED_LOCATION) {
                this.bed_location = event.getBlock().getLocation();
                this.setEtape(CreationEtapes.CREATION_END);
            }
        }

        public boolean isFinish() {
            if (this.m_etape == CreationEtapes.CREATION_END) {
                return true;
            }
            return false;
        }

        private void setEtape(CreationEtapes etape) {
            this.m_etape = etape;
            this.m_player.sendMessage(this.m_etape.getMessage());
            if (this.isFinish()) {
                TeamManager.TucTeam team = TeamManager.getInstance().getTeam(this.m_color);
                team.setSpawnPlayer(this.spawn_player);
                team.setSpawnItem(this.spawn_item);
                team.setBedLocation(this.bed_location);
            }
        }

    }

}

