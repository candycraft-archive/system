package de.pauhull.bansystem.spigot;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import de.pauhull.bansystem.spigot.abstraction.IReportInventory;
import de.pauhull.bansystem.spigot.command.*;
import de.pauhull.bansystem.spigot.listener.PlayerCommandPreprocessListener;
import de.pauhull.bansystem.spigot.listener.PlayerJoinListener;
import de.pauhull.bansystem.spigot.listener.PlayerLoginListener;
import de.pauhull.bansystem.spigot.listener.PlayerQuitListener;
import de.pauhull.bansystem.spigot.util.PluginMessageListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SpigotBanSystem extends JavaPlugin {

    @Getter
    private static SpigotBanSystem instance;

    @Getter
    private FileConfiguration config;

    @Getter
    private IReportInventory reportInventory;

    @Getter
    private String maintenanceMessage;

    @Getter
    private String maintenancePermission;

    @Getter
    private boolean maintenance;

    @Getter
    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        instance = this;

        this.config = copyAndLoad("config.yml", new File(getDataFolder(), "config.yml"));

        this.maintenance = config.getBoolean("Maintenance");
        this.maintenanceMessage = ChatColor.translateAlternateColorCodes('&', config.getString("Message"));
        this.maintenancePermission = config.getString("Permission");

        if (Bukkit.getVersion().contains("1.13")) {
            this.reportInventory = new de.pauhull.bansystem.spigot.v1_13_R1.ReportInventory(this);
        } else {
            this.reportInventory = new de.pauhull.bansystem.spigot.v1_8_R3.ReportInventory(this);
        }

        new CopyCommand(this);
        new ClearChatCommand(this);
        new HelpCommand(this);
        new RankCommand(this);
        new ReportCommand(this);
        new PlayerJoinListener(this);
        new VanishCommand(this);
        new PlayerCommandPreprocessListener(this);
        new PlayerQuitListener(this);
        new PlayerLoginListener(this);
        new PluginMessageListener(this);

        if (Bukkit.getVersion().contains("1.8")) {
            new CrashCommand(this);
        }

        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.TAB_COMPLETE) {
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
                    try {
                        if (event.getPlayer().hasPermission("lib.commandtab.bypass")) {
                            return;
                        }
                        PacketContainer packet = event.getPacket();
                        String message = packet.getSpecificModifier(String.class).read(0).toLowerCase();
                        if (message.startsWith("/")) {
                            event.setCancelled(true);
                        }
                    } catch (FieldAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private YamlConfiguration copyAndLoad(String resource, File file) {

        if (!file.exists()) {
            file.getParentFile().mkdirs();

            try {
                Files.copy(getResource(resource), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return YamlConfiguration.loadConfiguration(file);
    }

}
