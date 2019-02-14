package de.godtitan.bansystem.spigot.listener;

import de.godtitan.bansystem.spigot.SpigotBanSystem;
import de.godtitan.bansystem.spigot.command.VanishCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Paul
 * on 01.01.2019
 *
 * @author pauhull
 */
public class PlayerQuitListener implements Listener {

    private SpigotBanSystem plugin;

    public PlayerQuitListener(SpigotBanSystem plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        VanishCommand.getVanished().remove(event.getPlayer());
    }

}
