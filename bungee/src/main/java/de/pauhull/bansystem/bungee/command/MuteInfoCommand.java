package de.pauhull.bansystem.bungee.command;

import de.pauhull.bansystem.bungee.BungeeBanSystem;
import de.pauhull.bansystem.common.util.Messages;
import de.pauhull.bansystem.common.util.MuteInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Paul
 * on 17.03.2019
 *
 * @author pauhull
 */
public class MuteInfoCommand extends Command {

    private BungeeBanSystem plugin;

    public MuteInfoCommand(BungeeBanSystem plugin) {
        super("muteinfo");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("system.muteinfo")) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + Messages.NO_PERMISSIONS));
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§c/muteinfo <Spieler>"));
            return;
        }

        plugin.getUuidFetcher().fetchProfileAsync(args[0], (name, uuid) -> {
            if (name == null || uuid == null) {
                sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§cDieser Spieler existiert nicht"));
                return;
            }

            MuteInfo info = null;
            for (MuteInfo muteInfo : plugin.getMutes()) {
                if (muteInfo.getUuid().equals(uuid)) {
                    info = muteInfo;
                    break;
                }
            }

            if (info == null) {
                sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§cDieser Spieler ist nicht gemutet"));
                return;
            }

            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Hier kannst du den Chatlog von §c" + name + "§7 sehen: §ehttps://candycraft.de/mute/?id=" + info.getMuteId()));
        });
    }

}
