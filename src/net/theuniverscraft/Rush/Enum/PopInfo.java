package net.theuniverscraft.Rush.Enum;

import java.util.ArrayList;
import java.util.List;

import net.theuniverscraft.Rush.Utils.Translation;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum PopInfo {
	CLAY(ChatColor.AQUA + "Bronze", 1L, Material.CLAY_BRICK),
	IRON(ChatColor.AQUA + "Argent", 15L, Material.IRON_INGOT),
	GOLD(ChatColor.AQUA + "Or", 60L, Material.GOLD_INGOT),
	EMERALD(ChatColor.AQUA + "Emeraude", 300L, Material.EMERALD);
	
	private ItemStack m_is;
	private long m_pop;
	
	PopInfo(String name, long pop, Material type) {
		m_pop = pop;
		
		m_is = new ItemStack(type, 1);
		ItemMeta meta = m_is.getItemMeta();
		
		if(name != null) {
			meta.setDisplayName(name);
			List<String> lore = new ArrayList<String>();
			lore.add(Translation.LORE_POP);
			meta.setLore(lore);
		}
		
		m_is.setItemMeta(meta);
	}
	
	public ItemStack getIs() { return m_is.clone(); }
	public long getPop() { return m_pop; }
}
