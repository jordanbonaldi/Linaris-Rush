
package net.theuniverscraft.Rush.Utils;

import net.theuniverscraft.Rush.Enum.TeamColor;
import net.theuniverscraft.Rush.Managers.TeamManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public abstract class Translation {
    public static final String BED_DESTROY = (Object)ChatColor.GOLD + "Le lit <team>" + (Object)ChatColor.GOLD + " a �t� d�truit !";
    public static final String YOUR_BED_DESTROY = (Object)ChatColor.RED + "Votre lit a �t� d�truit !";
    public static final String YOU_DESTROY_BED = (Object)ChatColor.DARK_GREEN + "Vous avez detruit le lit <team>";
    public static final String GAME_ALREADY_START = (Object)ChatColor.DARK_RED + "La partie est d�j\u00e0 commenc�e !";
    public static final String ALL_TEAM_NOT_OK = (Object)ChatColor.DARK_RED + "Toutes les teams ne sont pas configur�";
    public static final String CHOOSE_YOUR_TEAM = (Object)ChatColor.DARK_GREEN + "Choisissez vos teams !";
    public static final String YOU_ARE_IN_TEAM = "�6Vous avez rejoint la team �6<team> �7(�a<x>�7/�a<max>�7)";
    public static final String TEAM_ARE_FULL = "�6La team �b<team> est pleine �7(�a<x>�7/�a<max>�7)";
    public static final String GAME_FULL = (Object)ChatColor.RED + "La partie est pleine !";
    public static final String THE_GAME_BEGIN_IN = ChatColor.GOLD + "La partie commence dans " + ChatColor.AQUA + "<sec> secondes";
    public static final String THE_GAME_BEGIN_IN_ONE = ChatColor.GOLD + "La partie commence dans " + ChatColor.AQUA + "1 seconde";
    public static final String THE_GAME_BEGIN = "�aC'est parti :D ! Que le Meilleur gagne !";
    public static final String YOU_HAVE_DESTROY_BED = "�eVous avez d�truit le lit <team>";
    public static final String YOUR_BED_ARE_DESTROY = (Object)ChatColor.RED + "La team <team>" + (Object)ChatColor.RED + " a d�truit votre lit !";
    public static final String A_BED_DESTROY = (Object)ChatColor.GRAY + "La team <enemy> " + (Object)ChatColor.GRAY + " a detruit le lit <victim>";
    public static final String YOU_RESPAWN_IN = (Object)ChatColor.GOLD + "Vous r�aparaitrez dans <sec> secondes !";
    public static final String YOU_RESPAWN_IN_ONE = (Object)ChatColor.GOLD + "Vous r�aparaitrez dans 1 seconde !";
    public static final String YOU_RESPAWN = (Object)ChatColor.GOLD + "Vous �tes r�aparu !";
    public static final String YOU_CANNOT_PLACE_TNT = (Object)ChatColor.RED + "Vous ne pouvez pas mettre de TNT si pr\u00e8s de votre lit !";
    public static final String YOU_CANNOT_BUILD = (Object)ChatColor.RED + "Vous ne pouvez pas construire si pr\u00e8s de votre lit !";
    public static final String THE_GAME_IS_END = (Object)ChatColor.AQUA + "La partie est fini !";
    public static final String YOU_ARE_DEFINITIVLY_DEAD = (Object)ChatColor.RED + "Vous �tes d�finitivement mort !";
    public static final String MOTD_BEGIN_IN = (Object)ChatColor.AQUA + "<sec> secondes";
    public static final String MOTD_GAME = (Object)ChatColor.RED + "En cours ...";
    public static final String MOTD_END_GAME = (Object)ChatColor.RED + "Partie termin�e";
    public static final String LORE_POP = (Object)ChatColor.RESET + "Echange cette ressource aux pnj";
    public static final String LORE_SKULL = (Object)ChatColor.RESET + "Clique pour te t�l�porter";
    public static final String TEAM_IS_TOO_FULL = (Object)ChatColor.RED + "Cette team contient trop de joueurs par rapport \u00e0 l'autre team";
    public static final String JOIN_MESSAGE = ChatColor.YELLOW + "�6<player> �7a rejoint le jeu";
    public static final String QUIT_MESSAGE = ChatColor.YELLOW + "�6<player> �7a quitt� le jeu";

    public static void broadcastWinner(TeamManager.TucTeam winner) {
        if (winner != null) {
            final TeamColor color = winner.getColor();
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage("�f�m----------------------------");
            Bukkit.broadcastMessage(ChatColor.GOLD + "   L'equipe des " + color.getChatColor() + color.toLocal()+"s" + ChatColor.GOLD + " a gagn� !");
            Bukkit.broadcastMessage("�f�m----------------------------");
            Bukkit.broadcastMessage("");
        }
        else {
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage("�f�m----------------------------");
            Bukkit.broadcastMessage("   �7La partie est termin� ! &eRetour au hub :)");
            Bukkit.broadcastMessage("�f�m----------------------------");
            Bukkit.broadcastMessage("");
        }
    }
}

