package de.godtitan.bansystem.bungee.listener;

import de.godtitan.bansystem.bungee.BungeeBanSystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Paul
 * on 30.11.2018
 *
 * @author pauhull
 */
public class ProxyPingListener implements Listener {

    private BungeeBanSystem plugin;

    public ProxyPingListener(BungeeBanSystem plugin) {
        this.plugin = plugin;

        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        if (plugin.isMaintenance()) {
            ServerPing ping = event.getResponse();
            ping.setVersion(new ServerPing.Protocol("§cWartungsarbeiten", 2));
            event.setResponse(ping);
        }
    }

}
