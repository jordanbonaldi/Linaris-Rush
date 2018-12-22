package net.theuniverscraft.Rush.Enum;

import net.theuniverscraft.Rush.Utils.Constantes;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public enum TeamColor {
	RED("Rouge", ChatColor.RED, DyeColor.RED),
	BLUE("Bleu", ChatColor.BLUE, DyeColor.BLUE),
	YELLOW("Jaune", ChatColor.YELLOW, DyeColor.YELLOW),
	GREEN("Verte", ChatColor.GREEN, DyeColor.GREEN);
	
	private String m_local;
	private ChatColor m_chatColor;
	private DyeColor m_dyeColor;
	
	TeamColor(String local, ChatColor chatColor, DyeColor dyeColor) {
		m_local = local;
		m_chatColor = chatColor;
		m_dyeColor = dyeColor;
	}
	
	public ChatColor getChatColor() { return m_chatColor; }
	public DyeColor getDyeColor() { return m_dyeColor; }
	public String toLocal() { return m_local; }
	
	public static TeamColor getByeDyeColor(DyeColor dyeColor) {
		for(TeamColor color : values()) {
			if(color.getDyeColor().equals(dyeColor)) return color;
		}
		return null;
	}
	
	public static TeamColor getByeChatColor(ChatColor chatColor) {
		for(TeamColor color : values()) {
			if(color.getChatColor().equals(chatColor)) return color;
		}
		return null;
	}
	
	public static TeamColor[] valuesAvailable() {
		TeamColor[] values = new TeamColor[Constantes.TEAM_NUMBER];
		
		for(int i = 0; i < Constantes.TEAM_NUMBER; i++) {
			values[i] = values()[i];
		}
		
		return values;
	}
}
