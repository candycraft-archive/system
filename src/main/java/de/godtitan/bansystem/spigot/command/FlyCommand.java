package de.godtitan.bansystem.spigot.command;

import de.godtitan.bansystem.common.util.Messages;
import de.godtitan.bansystem.spigot.SpigotBanSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Paul
 * on 01.01.2019
 *
 * @author pauhull
 */
public class FlyCommand implements CommandExecutor {

    private SpigotBanSystem plugin;

    public FlyCommand(SpigotBanSystem plugin) {
        this.plugin = plugin;

        plugin.getCommand("fly").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("system.fly")) {
            sender.sendMessage(Messages.BAN_PREFIX + Messages.NO_PERMISSIONS);
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.BAN_PREFIX + Messages.ONLY_PLAYERS);
            return true;
        }
        Player player = (Player) sender;

        player.setAllowFlight(!player.getAllowFlight());
        if (player.getAllowFlight()) {
            player.sendMessage(Messages.PREFIX + "Du kannst nun §afliegen§7!");
        } else {
            player.sendMessage(Messages.PREFIX + "Du kannst nun §cnicht §7mehr fliegen!");
        }

        return true;
    }

}
