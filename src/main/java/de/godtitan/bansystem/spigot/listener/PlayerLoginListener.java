package de.godtitan.bansystem.spigot.listener;

import de.godtitan.bansystem.spigot.SpigotBanSystem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * Created by Paul
 * on 01.12.2018
 *
 * @author pauhull
 */
public class PlayerLoginListener implements Listener {

    private SpigotBanSystem plugin;

    public PlayerLoginListener(SpigotBanSystem plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (plugin.isMaintenance() && !event.getPlayer().hasPermission(plugin.getMaintenancePermission())) {
            event.setKickMessage(plugin.getMaintenanceMessage());
            event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
        }
    }

}
