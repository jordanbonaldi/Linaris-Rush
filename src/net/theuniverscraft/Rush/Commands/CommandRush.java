package net.theuniverscraft.Rush.Commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.theuniverscraft.Rush.Rush;
import net.theuniverscraft.Rush.Enum.GameState;
import net.theuniverscraft.Rush.Enum.TeamColor;
import net.theuniverscraft.Rush.Managers.CreateTeamManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class CommandRush implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "Vous devez être op");
			return true;
		}
		
		// Commande console
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("config")) {
				if(Rush.getInstance().getGameState() == GameState.BEFORE_GAME) {
					sender.sendMessage(ChatColor.GREEN + "Jeu en config !");
					for(Player aplayer : Bukkit.getOnlinePlayers()) {
						if(!aplayer.isOp()) aplayer.kickPlayer(ChatColor.RED + "Jeu en config !");
					}
					Rush.getInstance().setGameState(GameState.INIT_GAME);
				}
				else {
					sender.sendMessage(ChatColor.RED + "Vous ne pouvez pas faire cela maintenant !");
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("forcestart")) {
				Rush.getInstance().setGameState(GameState.BEFORE_GAME);
				return true;
			}
			else if(args[0].equalsIgnoreCase("clear")) {
				for(Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
					if(entity instanceof Item) {
						entity.remove();
					}
					else if(entity instanceof Player) {
						Player aplayer = (Player) entity;
						aplayer.setMaxHealth(20D);
						aplayer.setHealth(20D);
						aplayer.setFoodLevel(20);
						
						Collection<PotionEffect> effects = aplayer.getActivePotionEffects();
						for(PotionEffect effect : effects) {
							aplayer.removePotionEffect(effect.getType());
						}
					}
				}
				return true;
			}
		}
		else if(args.length == 2) {
			if(args[0].equalsIgnoreCase("teams")) {
				try {
					int teams = Integer.parseInt(args[1]);
					Rush.getInstance().getConfig().set("teams", teams);
					Rush.getInstance().saveConfig();
				} catch(Exception e) {
					sender.sendMessage(ChatColor.DARK_RED + "Vous devez entrer un nombre");
				}
			}
		}
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Vous devez être un joueur");
			return true;
		}
		
		Player player = (Player) sender;
		
		// Commande admin IG
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("setspawn") || args[0].equalsIgnoreCase("setlobby")) {
				Rush.getInstance().setLobby(player.getLocation());
				player.sendMessage(ChatColor.GREEN + "Ok !");
				return true;
			}
		}
		else if(args.length == 2) {
			if(args[0].equalsIgnoreCase("configteam") || args[0].equalsIgnoreCase("createteam")) {
				List<TeamColor> colorAllow = new ArrayList<TeamColor>();
				for(TeamColor color : TeamColor.valuesAvailable()) {
					colorAllow.add(color);
				}
								
				try {
					TeamColor color = TeamColor.valueOf(args[1]);
					
					if(colorAllow.contains(color)) CreateTeamManager.getInstance().createTeam(player, color);
					else throw new IllegalArgumentException();
				} catch(IllegalArgumentException e) {
					player.sendMessage(ChatColor.GRAY + "Couleurs disponnibles :");
					for(TeamColor color : colorAllow) {
						player.sendMessage(ChatColor.GRAY + " - " + color.getChatColor() + color.toString());
					}
				}
				return true;
			}
		}
		
		return false;
	}

}
