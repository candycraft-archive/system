package de.godtitan.bansystem.bungee.listener;

import de.godtitan.bansystem.bungee.BungeeBanSystem;
import de.godtitan.bansystem.bungee.command.TeamChatCommand;
import de.godtitan.bansystem.common.util.Messages;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Paul
 * on 28.11.2018
 *
 * @author pauhull
 */
public class PlayerDisconnectListener implements Listener {

    private BungeeBanSystem plugin;

    public PlayerDisconnectListener(BungeeBanSystem plugin) {
        this.plugin = plugin;

        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (player.hasPermission("teamchat.use")) {
            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                if (all.hasPermission("teamchat.use") && !TeamChatCommand.getDisabled().contains(all.getName())) {
                    all.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "Das Teammitglied §e" + player.getName() + " §7ist nun §coffline§7!"));
                }
            }
        }

        if (plugin.getReport().isReporting(player.getName())) {
            plugin.getReport().setReporting(player.getName(), null);
        }
        plugin.getPlaytimeTable().logDisconnect(player.getUniqueId());
    }

}
