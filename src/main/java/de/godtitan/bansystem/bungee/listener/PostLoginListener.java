package de.godtitan.bansystem.bungee.listener;

import de.godtitan.bansystem.bungee.BungeeBanSystem;
import de.godtitan.bansystem.bungee.command.TeamChatCommand;
import de.godtitan.bansystem.common.util.Messages;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by Paul
 * on 01.01.2019
 *
 * @author pauhull
 */
public class PostLoginListener implements Listener {

    private BungeeBanSystem plugin;

    public PostLoginListener(BungeeBanSystem plugin) {
        this.plugin = plugin;

        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
            if (event.getPlayer() == null)
                return;

            if (event.getPlayer().hasPermission("teamchat.use")) {
                for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                    if (all.hasPermission("teamchat.use") && !TeamChatCommand.getDisabled().contains(all.getName())) {
                        all.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "Das Teammitglied §e" + event.getPlayer().getName() + " §7ist nun §aonline§7!"));
                    }
                }
            }
        }, 100, TimeUnit.MILLISECONDS);
    }

}
