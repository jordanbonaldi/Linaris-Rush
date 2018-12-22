
package net.theuniverscraft.Rush.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.theuniverscraft.Rush.Rush;
import net.theuniverscraft.Rush.Enum.GameState;
import net.theuniverscraft.Rush.Enum.TeamColor;
import net.theuniverscraft.Rush.Managers.PlayersManager;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public abstract class Utils {
    public static Location getLocation(ConfigurationSection section) {
        try {
            Location location = new Location(Bukkit.getWorld((String)section.getString("world")), section.getDouble("x"), section.getDouble("y"), section.getDouble("z"), (float)section.getDouble("yaw"), (float)section.getDouble("pitch"));
            return location;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static void setLocation(ConfigurationSection section, Location loc) {
        try {
            if (loc == null) {
                return;
            }
            section.set("world", (Object)loc.getWorld().getName());
            section.set("x", (Object)loc.getX());
            section.set("y", (Object)loc.getY());
            section.set("z", (Object)loc.getZ());
            section.set("yaw", (Object)Float.valueOf(loc.getYaw()));
            section.set("pitch", (Object)Float.valueOf(loc.getPitch()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean plus1moins1equal(int x, int y) {
        if (x - y >= -1 && x - y <= 1) {
            return true;
        }
        return false;
    }

    public static void allToLobby() {
        Player[] arrplayer = Bukkit.getOnlinePlayers();
        int n = arrplayer.length;
        int n2 = 0;
        while (n2 < n) {
            Player player = arrplayer[n2];
            player.teleport(Rush.getInstance().getLobby());
            ++n2;
        }
    }

    public static Object getMetadata(Metadatable meta, String key) {
        if (meta.hasMetadata(key)) {
            for (MetadataValue value : meta.getMetadata(key)) {
                if (!value.getOwningPlugin().getName().equalsIgnoreCase(Rush.getInstance().getName())) continue;
                return value.value();
            }
        }
        return null;
    }

    public static void setInventory(final Player player) {
        PlayersManager.TucPlayer tplayer = PlayersManager.getInstance().getPlayer(player);
        PlayerInventory inv = player.getInventory();
        GameState state = Rush.getInstance().getGameState();
        if (state == GameState.BEFORE_GAME) {
            inv.clear();
            int i = 0;
            while (i < TeamColor.valuesAvailable().length) {
                ItemStack is = new ItemStack(Material.WOOL, 1);
                is.setDurability((short)TeamColor.values()[i].getDyeColor().getData());
                inv.setItem(i, is);
                ++i;
            }
            ItemStack is_kit = new ItemStack(Material.NETHER_STAR);
            ItemMeta is_kit_meta = is_kit.getItemMeta();
            is_kit_meta.setDisplayName("§6Choisissez un kit");
            is_kit.setItemMeta(is_kit_meta);
            inv.setItem(8, is_kit);
        } else if (tplayer.isSpectator()) {
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Rush.getInstance(), new Runnable(){

                @Override
                public void run() {
                	player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
                }
            }, 5);
            Utils.updateSpectator(player);
        } else if (state == GameState.GAME) {
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            inv.clear();
            ItemStack[] armor = new ItemStack[4];
            int i = 0;
            while (i < 4) {
                ItemStack item = null;
                switch (i) {
                    case 0: {
                        item = new ItemStack(Material.LEATHER_BOOTS);
                        break;
                    }
                    case 1: {
                        item = new ItemStack(Material.LEATHER_LEGGINGS);
                        break;
                    }
                    case 3: {
                        item = new ItemStack(Material.LEATHER_HELMET);
                    }
                }
                if (item != null) {
                    LeatherArmorMeta meta = (LeatherArmorMeta)item.getItemMeta();
                    meta.setColor(tplayer.getTeam().getColor().getDyeColor().getColor());
                    item.setItemMeta((ItemMeta)meta);
                    item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                    item.addEnchantment(Enchantment.DURABILITY, 3);
                }
                armor[i] = item;
                ++i;
            }
            inv.setArmorContents(armor);
            Rush.getGameAPI().applyKit(player);
        }
        player.updateInventory();
    }

    public static void updateSpectators() {
        for (PlayersManager.TucPlayer tplayer : PlayersManager.getInstance().getSpectator()) {
            Utils.updateSpectator(tplayer.getPlayer());
        }
    }

    private static void updateSpectator(Player player) {
        List<PlayersManager.TucPlayer> players = PlayersManager.getInstance().getPlayersNotDead();
        int i = 0;
        while (i < players.size()) {
            ItemStack is = new ItemStack(Material.SKULL_ITEM);
            is.setDurability((short) 3);
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName((Object)players.get(i).getTeam().getColor().getChatColor() + players.get(i).getPlayer().getName());
            ArrayList<String> lore = new ArrayList<String>();
            lore.add(Translation.LORE_SKULL);
            meta.setLore(lore);
            is.setItemMeta(meta);
            player.getInventory().setItem(i, is);
            ++i;
        }
        player.updateInventory();
    }

    public static <T> void sortList(List<T> list, Comparator<T> c) {
        int i = 0;
        while (i < list.size() - 1) {
            T obj2;
            T obj1 = list.get(i);
            if (c.compare(obj1, obj2 = list.get(i + 1)) > 0) {
                list.set(i, obj2);
                list.set(i + 1, obj1);
                if ((i -= 2) <= -2) {
                    ++i;
                }
            }
            ++i;
        }
    }
    
    public static void tpToLobby(final Player player) {
   	 ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF("Hub1");
        player.sendPluginMessage(Rush.getInstance(), "BungeeCord", out.toByteArray());
   }

}

