package de.pauhull.bansystem.spigot.listener;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.pauhull.bansystem.spigot.SpigotBanSystem;
import de.pauhull.bansystem.spigot.command.VanishCommand;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.lang.reflect.Field;
import java.util.Calendar;
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

    private static final String STYLEX_VALUE = "eyJ0aW1lc3RhbXAiOjE1NTQwNDI1MTcwNDEsInByb2ZpbGVJZCI6IjU4ZTk3MDI3OGNhODQ4ODhhNzU2MjZlNzE5ZjIxZGUwIiwicHJvZmlsZU5hbWUiOiJsZVN0eWxleCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGYxN2JmMjFmZTNkYjRjZTg5ZWY1OWM2MTE0Mjk0OTA1NTgxOGVlNDg3YzI1ODhlMmI4YWEyOTI5YjRjMjQ0NSJ9fX0=";
    private static final String STYLEX_SIGNATURE = "bvjHhSL8wTgey63Rt/U/Y+yWpe4YSBm319tmhRaD1y5tLEcweIrKHxlN5KLPIsOR2uYt0z7acKxSSrvbGnfSsi602fVM84OTW2AtEfDvwWMpdKRgOS/aXJ8OVFOS7w1P8OXWxDtOPTtvpum8LK/gsQRtZ3qZW4oBkytlBd7Q9g6hB78UEYujY72qTI72zLJgNxCK//HxufWgXQVvdgJt9UD0q23ZilwJDBWkXdXol8qPB7rsR24LU+MA4KTevVK5uGmr3n8ptExl3dkSldl4viAdJaKqMylC0xaE6T1I9rJkl34t37K2G9U6LgdDrXHypiVQZSmaMbpzDU9oNghr3/xkVRE4SVXy56XY8UmmHTGVyGT/z9LL4OODxp2D16kYTYFQa721wm1GjQYWeTOw6uH7cCD0/GDJ/JG5h0BPMXlB6aparM6RebHvom2B6LXknXt5obPaANlJTzj1Pq3xUttD73E1n0cSD+QB4qqlCvbr31dYDfxbQV9gOzBLzWUZUj3cTFUJzC/Zm0C58VOs3FxcvOvOQwEv/vQ/vsfLC7RBrqsU/eQojb8VNKWjSd4i8wFdAkkWC+FHOSrXT/p0S5fP0bGc1aPc/6VuXL/g/QCCmEHRimUGqbzaGyK2PdGPxjIXGHoGlhsDksW+7/aPZn5T8GDMOR925bv92SJ52pM=";
    private static final Property STYLEX_PROPERTY = new Property("textures", STYLEX_VALUE, STYLEX_SIGNATURE);

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

        GameProfile profile = new GameProfile(reporting.getUniqueId(), reporting.getName());
        profile.getProperties().put("textures", STYLEX_PROPERTY);

        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        if (month == 4 && day == 1) {
            try {
                Object handle = reporting.getClass().getMethod("getHandle").invoke(reporting);
                Field bH = handle.getClass().getSuperclass().getDeclaredField("bH");
                bH.setAccessible(true);
                bH.set(handle, profile);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
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
