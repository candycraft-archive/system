package de.pauhull.bansystem.spigot.command;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import de.pauhull.bansystem.common.util.Messages;
import de.pauhull.bansystem.spigot.SpigotBanSystem;
import de.pauhull.uuidfetcher.common.communication.message.RunCommandMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportCommand implements CommandExecutor {

    private SpigotBanSystem plugin;

    public ReportCommand(SpigotBanSystem plugin) {
        this.plugin = plugin;

        plugin.getCommand("report").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("quit")) {
            if (sender.hasPermission("system.getreport")) {
                new RunCommandMessage(player.getName(), "reportbungee quit").sendToProxy("Proxy");
            } else {
                player.sendMessage(Messages.BAN_PREFIX + Messages.NO_PERMISSIONS);
            }
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("review")) {
            if (sender.hasPermission("system.getreport")) {
                if (args.length > 1) {
                    new RunCommandMessage(player.getName(), "reportbungee review " + args[1]).sendToProxy("Proxy");
                } else {
                    player.sendMessage(Messages.BAN_PREFIX + Messages.NO_PERMISSIONS);
                }
            } else {
                player.sendMessage(Messages.BAN_PREFIX + Messages.NO_PERMISSIONS);
            }
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(Messages.BAN_PREFIX + "§c/report <Spieler>");
            return true;
        }

        if (args[0].equalsIgnoreCase(player.getName())) {
            player.sendMessage(Messages.BAN_PREFIX + "Du kannst dich §cnicht §7selbst reporten!");
            return true;
        }

        PlayerObject cloudPlayer = TimoCloudAPI.getUniversalAPI().getPlayer(args[0]);
        if (cloudPlayer == null) {
            player.sendMessage(Messages.BAN_PREFIX + "Dieser Spieler ist §cnicht §7online!");
            return true;
        }

        plugin.getReportInventory().show(player, args[0]);

        return true;
    }

}
