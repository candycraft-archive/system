package de.pauhull.bansystem.spigot.command;

import de.pauhull.bansystem.spigot.SpigotBanSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Paul
 * on 01.01.2019
 *
 * @author pauhull
 */
public class HelpCommand implements CommandExecutor {

    private SpigotBanSystem plugin;

    public HelpCommand(SpigotBanSystem plugin) {
        this.plugin = plugin;
        plugin.getCommand("help").setExecutor(this);
        plugin.getCommand("?").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§f► §d§lCandyCraft§f ◄");
        sender.sendMessage("§e/report <Spieler> §8» §7Melde einen Spieler");
        sender.sendMessage("§e/hub|l §8» §7Gehe zur Lobby zurück");
        sender.sendMessage("§e/forum §8» §7Unser Forum");
        sender.sendMessage("§e/ts §8» §7Unser TeamSpeak 3");
        sender.sendMessage("§e/youtuber §8» §7Vorraussetzungen für den Youtuber Rang");
        sender.sendMessage("§e/support §8» §7Supportanfrage senden");
        sender.sendMessage("§eWenn du noch Fragen hast, wende dich gerne an das Team!");
        return true;
    }

}
