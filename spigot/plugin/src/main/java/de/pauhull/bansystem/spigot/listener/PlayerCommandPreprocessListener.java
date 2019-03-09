package de.pauhull.bansystem.spigot.listener;

import de.pauhull.bansystem.spigot.SpigotBanSystem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Created by Paul
 * on 01.01.2019
 *
 * @author pauhull
 */
public class PlayerCommandPreprocessListener implements Listener {

    public PlayerCommandPreprocessListener(SpigotBanSystem plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/?")) {
            Bukkit.dispatchCommand(event.getPlayer(), "help");
            event.setCancelled(true);
        }
    }

}
