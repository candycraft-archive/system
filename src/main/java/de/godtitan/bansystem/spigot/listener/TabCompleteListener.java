package de.godtitan.bansystem.spigot.listener;

import de.godtitan.bansystem.spigot.SpigotBanSystem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;

/**
 * Created by Paul
 * on 18.02.2019
 *
 * @author pauhull
 */
public class TabCompleteListener implements Listener {

    private SpigotBanSystem plugin;

    public TabCompleteListener(SpigotBanSystem plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onTabComplete(PlayerChatTabCompleteEvent event) {
        if (event.getChatMessage().startsWith("/")) {
            event.getTabCompletions().clear();
        }
    }

}
