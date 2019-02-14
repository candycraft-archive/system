package de.godtitan.bansystem.bungee.command;

import de.godtitan.bansystem.bungee.BungeeBanSystem;
import de.godtitan.bansystem.common.util.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BroadcastCommand extends Command {

    private BungeeBanSystem plugin;

    public BroadcastCommand(BungeeBanSystem plugin) {
        super("broadcast", null, "bc");
        this.plugin = plugin;

        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("system.bc")) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + Messages.NO_PERMISSIONS));
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "§c/bc <Nachricht...>"));
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                stringBuilder.append(" ");
            }

            stringBuilder.append(args[i]);
        }
        BaseComponent[] message = TextComponent.fromLegacyText("§8[§e§lBroadcast§8] §f" + ChatColor.translateAlternateColorCodes('&', stringBuilder.toString()));

        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            player.sendMessage(message);
        }
    }
}
