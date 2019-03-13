package de.pauhull.bansystem.bungee.listener;

import de.pauhull.bansystem.bungee.BungeeBanSystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Paul
 * on 13.03.2019
 *
 * @author pauhull
 */
public class ServerKickListener implements Listener {

    private BungeeBanSystem plugin;

    public ServerKickListener(BungeeBanSystem plugin) {
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onServerKick(ServerKickEvent event) {
        ProxiedPlayer player = event.getPlayer();
        player.sendMessage(TextComponent.fromLegacyText(event.getKickReason()));
    }

}
