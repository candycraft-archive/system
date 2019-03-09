package de.pauhull.bansystem.spigot.command;

import de.pauhull.bansystem.common.util.Messages;
import de.pauhull.bansystem.spigot.SpigotBanSystem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul
 * on 01.01.2019
 *
 * @author pauhull
 */
public class VanishCommand implements CommandExecutor {

    @Getter
    private static List<Player> vanished = new ArrayList<>();

    private SpigotBanSystem plugin;

    public VanishCommand(SpigotBanSystem plugin) {
        this.plugin = plugin;
        plugin.getCommand("vanish").setExecutor(this);
        plugin.getCommand("v").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("system.vanish")) {
            sender.sendMessage(Messages.BAN_PREFIX + Messages.NO_PERMISSIONS);
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.BAN_PREFIX + Messages.ONLY_PLAYERS);
            return true;
        }
        Player player = (Player) sender;

        if (!vanished.contains(player)) {
            player.sendMessage(Messages.BAN_PREFIX + "Du bist nun im §eVanish-Modus§7!");
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.hidePlayer(player);
            }
            vanished.add(player);
        } else {
            player.sendMessage(Messages.BAN_PREFIX + "Du bist nun nicht mehr im §eVanish-Modus§7!");
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.showPlayer(player);
            }
            vanished.remove(player);
        }

        return true;
    }

}
