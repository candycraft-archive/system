package de.godtitan.bansystem.spigot.command;

import de.godtitan.bansystem.common.util.Messages;
import de.godtitan.bansystem.spigot.SpigotBanSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ClearChatCommand implements CommandExecutor {

    private SpigotBanSystem plugin;

    public ClearChatCommand(SpigotBanSystem plugin) {
        this.plugin = plugin;

        plugin.getCommand("clearchat").setExecutor(this);
        plugin.getCommand("cc").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.hasPermission("bansystem.cc")) {
            sender.sendMessage(Messages.PREFIX + Messages.NO_PERMISSIONS);
            return true;
        }

        for (int i = 0; i < 100; i++) {
            Bukkit.broadcastMessage(" ");
        }

        Bukkit.broadcastMessage(Messages.PREFIX + "Der Chat wurde §egeleert§7!");
        Bukkit.broadcastMessage(" ");

        return true;
    }
}
