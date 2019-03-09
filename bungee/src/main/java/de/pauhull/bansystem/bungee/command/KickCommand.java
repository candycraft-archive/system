package de.pauhull.bansystem.bungee.command;

import de.pauhull.bansystem.bungee.BungeeBanSystem;
import de.pauhull.bansystem.common.util.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Paul
 * on 01.01.2019
 *
 * @author pauhull
 */
public class KickCommand extends Command {

    private BungeeBanSystem plugin;

    public KickCommand(BungeeBanSystem plugin) {
        super("kick");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("system.kick")) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + Messages.NO_PERMISSIONS));
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§c/kick <Spieler> <Grund...>"));
            return;
        }

        if (args[0].equalsIgnoreCase(sender.getName())) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Du kannst dich §cnicht §7selbst kicken!"));
            return;
        }

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + Messages.NOT_ONLINE));
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (builder.length() != 0) {
                builder.append(" ");
            }
            builder.append(args[i]);
        }
        String message = "§d§lCandyCraft.de\n§cDu wurdest vom Netwerk §4gekickt§c!";
        if (builder.length() > 0) {
            message += "§c\n\nGrund:§r " + ChatColor.translateAlternateColorCodes('&', builder.toString());
        }

        player.disconnect(TextComponent.fromLegacyText(message));

        for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
            if (all.hasPermission("teamchat.use") && !TeamChatCommand.getDisabled().contains(all.getName())) {
                all.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§e" + player.getName() + "§c wurde von §e" + sender.getName() + "§c gekickt!" +
                        (builder.length() > 0 ? (" Grund: §r" + ChatColor.translateAlternateColorCodes('&', builder.toString())) : "")));
            }
        }
    }

}
