package de.pauhull.bansystem.bungee.command;

import de.pauhull.bansystem.bungee.BungeeBanSystem;
import de.pauhull.bansystem.common.util.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Paul
 * on 15.03.2019
 *
 * @author pauhull
 */
public class MaintenanceCommand extends Command {

    private BungeeBanSystem plugin;

    public MaintenanceCommand(BungeeBanSystem plugin) {
        super("maintenance");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("system.maintenance.toggle")) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + Messages.NO_PERMISSIONS));
            return;
        }

        if (args.length < 1 || (!args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off"))) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§c/maintenance <on|off>"));
            return;
        }

        if (args[0].equalsIgnoreCase("on")) {
            plugin.setMaintenance(true);
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Wartungsarbeiten sind jetzt §caktiviert§7."));
        } else {
            plugin.setMaintenance(false);
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Wartungsarbeiten sind jetzt §adeaktiviert§7."));
        }
    }

}
