package de.pauhull.bansystem.bungee.command;

import de.pauhull.bansystem.bungee.BungeeBanSystem;
import de.pauhull.bansystem.common.util.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

/**
 * Created by Paul
 * on 01.01.2019
 *
 * @author pauhull
 */
public class UnmuteCommand extends Command {

    private BungeeBanSystem plugin;

    public UnmuteCommand(BungeeBanSystem plugin) {
        super("unmute");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("system.unmute")) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + Messages.NO_PERMISSIONS));
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§c/unmute <Spieler>"));
            return;
        }

        if (args[0].equalsIgnoreCase(sender.getName())) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Du kannst dich §cnicht §7selbst entmuten!"));
            return;
        }

        plugin.getUuidFetcher().fetchProfileAsync(args[0], (name, uuid) -> {

            if (uuid == null) {
                sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Dieser Spieler existiert §cnicht§7!"));
                return;
            }

            plugin.getMuteTable().getMute(uuid, muteInfo -> {

                if (muteInfo == null) {
                    sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Dieser Spieler ist §cnicht §7gemutet!"));
                    return;
                }

                plugin.getMuteTable().unmute(uuid);
                plugin.getMutes().removeIf(mute -> mute.getUuid().equals(uuid));

                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
                if (player != null) {
                    player.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Du bist §aentmutet§7!"));
                }

                UUID mutedBy = null;
                if (sender instanceof ProxiedPlayer) {
                    mutedBy = ((ProxiedPlayer) sender).getUniqueId();
                }

                for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                    if (all.getUniqueId().equals(mutedBy)
                            || (all.hasPermission("teamchat.use") && !TeamChatCommand.getDisabled().contains(all.getName()))) {

                        all.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§e" + name + "§a wurde von §e" + sender.getName() + "§a entmutet!"));
                    }
                }

            });
        });
    }

}
