package de.pauhull.bansystem.bungee.listener;

import de.pauhull.bansystem.bungee.BungeeBanSystem;
import de.pauhull.bansystem.bungee.command.TeamChatCommand;
import de.pauhull.bansystem.common.util.Messages;
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
        ProxiedPlayer player = event.getPlayer();
        player.sendMessage(TextComponent.fromLegacyText("§f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  "));
        player.sendMessage(TextComponent.fromLegacyText(" "));
        player.sendMessage(TextComponent.fromLegacyText("§7Hallo §c§l" + player.getName() + "§7 und willkommen auf §d§lCandyCraft§7!"));
        player.sendMessage(TextComponent.fromLegacyText(" "));
        player.sendMessage(TextComponent.fromLegacyText("§c§lSHOP§f http://shop.candycraft.de"));
        player.sendMessage(TextComponent.fromLegacyText("§c§lDISCORD§f https://discord.gg/gae5E7J"));
        player.sendMessage(TextComponent.fromLegacyText("§c§lTEAMSPEAK§f candycraft.de"));
        player.sendMessage(TextComponent.fromLegacyText("§c§lFORUM§f https://forum.candycraft.de"));
        player.sendMessage(TextComponent.fromLegacyText(" "));
        player.sendMessage(TextComponent.fromLegacyText("§f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  §c§m  §f§m  "));

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
        plugin.getPlaytimeTable().logJoin(player.getUniqueId());
    }

}
