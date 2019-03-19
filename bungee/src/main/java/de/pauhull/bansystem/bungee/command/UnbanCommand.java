package de.pauhull.bansystem.bungee.command;

import de.pauhull.bansystem.bungee.BungeeBanSystem;
import de.pauhull.bansystem.common.util.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class UnbanCommand extends Command {

    private BungeeBanSystem plugin;

    public UnbanCommand(BungeeBanSystem plugin) {
        super("unban", null, "pardon");
        this.plugin = plugin;

        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {

            if (!sender.hasPermission("system.unban")) {
                sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + Messages.NO_PERMISSIONS));
                return;
            }

            String unbannedName = args[0];

            plugin.getUuidFetcher().fetchUUIDAsync(unbannedName, uuid -> {

                if (uuid == null) {
                    sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Dieser Spieler §cexistiert nicht§7."));
                } else {
                    plugin.getBanTable().getBan(uuid, ban -> {
                        if (ban != null) {
                            plugin.getBanTable().unban(uuid);

                            plugin.getUuidFetcher().fetchNameAsync(uuid, name -> {
                                sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§e" + name + "§7 wurde §aentbannt§7!"));
                                for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                                    if (all == sender) continue;
                                    if (all.hasPermission("teamchat.use") && !TeamChatCommand.getDisabled().contains(all.getName())) {
                                        all.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§e" + sender.getName() + "§a hat §e" + name + "§2 entbannt§a!"));
                                    }
                                }
                            });
                        } else {
                            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Dieser Spieler ist §cnicht §7gebannt."));
                        }
                    });
                }

            });
        } else {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§c/unban <Spieler>"));
        }
    }
}
