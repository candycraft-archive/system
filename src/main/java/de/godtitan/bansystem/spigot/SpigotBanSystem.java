package de.godtitan.bansystem.spigot;

import de.godtitan.bansystem.spigot.command.*;
import de.godtitan.bansystem.spigot.inventory.ReportInventory;
import de.godtitan.bansystem.spigot.listener.*;
import de.godtitan.bansystem.spigot.util.PluginMessageListener;
import lombok.Getter;
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
    private ReportInventory reportInventory;

    @Getter
    private String maintenanceMessage;

    @Getter
    private String maintenancePermission;

    @Getter
    private boolean maintenance;

    @Override
    public void onEnable() {
        instance = this;

        this.config = copyAndLoad("config.yml", new File(getDataFolder(), "config.yml"));

        this.maintenance = config.getBoolean("Maintenance");
        this.maintenanceMessage = ChatColor.translateAlternateColorCodes('&', config.getString("Message"));
        this.maintenancePermission = config.getString("Permission");

        this.reportInventory = new ReportInventory(this);

        new CopyCommand(this);
        new ClearChatCommand(this);
        new HelpCommand(this);
        new CrashCommand(this);
        new RankCommand(this);
        new ReportCommand(this);
        new PlayerJoinListener(this);
        new VanishCommand(this);
        new PlayerCommandPreprocessListener(this);
        new PlayerQuitListener(this);
        new PlayerLoginListener(this);
        new PluginMessageListener(this);
        new TabCompleteListener(this);
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
