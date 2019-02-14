package de.godtitan.bansystem.spigot.listener;

import de.godtitan.bansystem.spigot.SpigotBanSystem;
import de.godtitan.bansystem.spigot.command.VanishCommand;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Paul
 * on 30.11.2018
 *
 * @author pauhull
 */
public class PlayerJoinListener implements Listener {

    @Getter
    private static Map<String, String> report = new HashMap<>();

    private static Map<Player, Player> reportSync = new HashMap<>();

    private SpigotBanSystem plugin;

    public PlayerJoinListener(SpigotBanSystem plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        schedule();
    }

    public static void report(Player reporting, Player reported) {
        reportSync.put(reporting, reported);
    }

    @EventHandler
    public void onVanishJoin(PlayerJoinEvent event) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (VanishCommand.getVanished().contains(online)) {
                event.getPlayer().hidePlayer(online);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player reporting = event.getPlayer();

        if (report.containsKey(reporting.getName())) {
            Player reported = Bukkit.getPlayer(report.get(reporting.getName()));
            report(reporting, reported);
            report.remove(reporting.getName());
        }
    }

    private void schedule() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            Iterator<Player> iterator = reportSync.keySet().iterator();

            while (iterator.hasNext()) {
                Player reporting = iterator.next();
                Player reported = reportSync.get(reporting);
                reportSync.remove(reporting);

                if (reporting == null) {
                    return;
                }

                reporting.setGameMode(GameMode.SPECTATOR);

                if (reported != null) {
                    reporting.teleport(reported);
                    reporting.setSpectatorTarget(reported);
                }
            }
        }, 0, 1);
    }

}
