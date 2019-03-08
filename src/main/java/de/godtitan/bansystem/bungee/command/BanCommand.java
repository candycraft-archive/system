package de.godtitan.bansystem.bungee.command;

import de.godtitan.bansystem.bungee.BungeeBanSystem;
import de.godtitan.bansystem.common.util.Messages;
import de.godtitan.bansystem.common.util.Reason;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class BanCommand extends Command {

    private BungeeBanSystem plugin;

    public BanCommand(BungeeBanSystem plugin) {
        super("ban");
        this.plugin = plugin;

        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("system.ban")) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + Messages.NO_PERMISSIONS));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§c/ban <Spieler> <Grund-ID>"));
            return;
        }

        Reason reason = null;

        try {
            int reasonID = Integer.parseInt(args[1]);
            reason = Reason.getFromID(reasonID);
        } catch (NumberFormatException ignored) {
        }

        if (reason == null) {
            sender.sendMessage(TextComponent.fromLegacyText(">§m                            §r<"));
            sender.sendMessage(TextComponent.fromLegacyText("  §cAlle Gründe:"));
            sender.sendMessage(TextComponent.fromLegacyText("  §cGrund-ID: Grund"));

            for (Reason allReasons : Reason.values()) {
                sender.sendMessage(TextComponent.fromLegacyText("  §c" + allReasons.getId() + ": " + allReasons.getReason()));
            }
            sender.sendMessage(TextComponent.fromLegacyText(">§m                            §r<"));
            return;
        }

        final Reason reasonFinal = reason;

        plugin.getUuidFetcher().fetchUUIDAsync(args[0], uuid -> {
            try {
                if (uuid != null) {

                    UUID bannedBy = UUID.fromString("1c6d932b-4715-4ab5-978e-944a8060426a"); //Konsole
                    if (sender instanceof ProxiedPlayer) {
                        bannedBy = ((ProxiedPlayer) sender).getUniqueId();
                    }

                    InetAddress ip = InetAddress.getByAddress(new byte[]{0, 0, 0, 0}); //0.0.0.0
                    ProxiedPlayer bannedPlayer = ProxyServer.getInstance().getPlayer(uuid);
                    if (bannedPlayer != null) {
                        ip = bannedPlayer.getAddress().getAddress();
                    }

                    plugin.getBanTable().ban(uuid, ip, bannedBy, reasonFinal);

                    plugin.getUuidFetcher().fetchNameAsync(uuid, name -> {
                        sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Du hast §e" + name + "§7 gebannt! Grund: §e" + reasonFinal.getReason()));
                        for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                            if (all.hasPermission("teamchat.use") && !TeamChatCommand.getDisabled().contains(all.getName())) {
                                all.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX+sender.getName() + " hat §e" + bannedPlayer.getName() + "§7 für §4" + reasonFinal.getReason() +"§7 gebannt"));
                            }
                        }
                        if (bannedPlayer != null) {
                            bannedPlayer.disconnect(TextComponent.fromLegacyText("§cDu wurdest gebannt. Grund: §e" + reasonFinal.getReason()));
                        }
                    });

                } else {
                    sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Dieser Spieler §cexistiert nicht§7!"));
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        });
    }
}
