package de.pauhull.bansystem.bungee.command;

import de.pauhull.bansystem.bungee.BungeeBanSystem;
import de.pauhull.bansystem.common.util.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class GotoCommand extends Command {

    private BungeeBanSystem bungeeBanSystem;

    public GotoCommand(BungeeBanSystem bungeeBanSystem) {
        super("goto");
        this.bungeeBanSystem = bungeeBanSystem;
        ProxyServer.getInstance().getPluginManager().registerCommand(bungeeBanSystem, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (!sender.hasPermission("system.ban")) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + Messages.NO_PERMISSIONS));
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "§c/goto <Spieler>"));
            return;
        }

        if (ProxyServer.getInstance().getPlayer(args[0]) != null) {
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            player.connect(target.getServer().getInfo());
            player.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "Du wurdest auf den Server von §e" + target.getName() + "§7 verschoben!"));
        } else {
            player.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "§cDieser Spieler ist nicht online"));
        }

    }
}
