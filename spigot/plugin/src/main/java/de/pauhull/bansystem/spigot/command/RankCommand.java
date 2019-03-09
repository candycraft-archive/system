package de.pauhull.bansystem.spigot.command;

import de.pauhull.bansystem.common.util.Messages;
import de.pauhull.bansystem.spigot.SpigotBanSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class RankCommand implements CommandExecutor {

    private SpigotBanSystem plugin;

    public RankCommand(SpigotBanSystem plugin) {
        this.plugin = plugin;

        plugin.getCommand("rank").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.hasPermission("bansystem.rank")) {
            sender.sendMessage(Messages.PREFIX + Messages.NO_PERMISSIONS);
            return true;
        }

        if (args.length == 2) {
            PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(args[1]);

            if (group != null) {
                PermissionUser user = PermissionsEx.getUser(args[0]);

                if (user == null) {
                    sender.sendMessage(Messages.PREFIX + "§cDieser Spieler existiert nicht.");
                    return true;
                }

                user.addGroup(group);
                sender.sendMessage(Messages.PREFIX + "§aRang erfolgreich gesetzt.");
            } else {
                sender.sendMessage(Messages.PREFIX + "§cDieser Rang existiert nicht.");
            }

            return true;
        } else if (args.length > 2) {
            PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(args[1]);

            if (group != null) {
                PermissionUser user = PermissionsEx.getUser(args[0]);

                if (user == null) {
                    sender.sendMessage(Messages.PREFIX + "§cDieser Spieler existiert nicht.");
                    return true;
                }

                long time;
                try {
                    time = Long.parseLong(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(Messages.PREFIX + "§cUngültiger Zahlenwert.");
                    return true;
                }

                user.addGroup(group.getName(), "*", time);
                sender.sendMessage(Messages.PREFIX + "§aRang erfolgreich gesetzt.");
            } else {
                sender.sendMessage(Messages.PREFIX + "§cDieser Rang existiert nicht.");
            }

            return true;
        }

        sender.sendMessage(Messages.PREFIX + "§c/rank <Spieler> <Rang> [Tage]");

        return true;
    }

}
