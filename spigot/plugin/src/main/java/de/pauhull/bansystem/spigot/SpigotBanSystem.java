package de.pauhull.bansystem.spigot;

import de.pauhull.bansystem.spigot.abstraction.IReportInventory;
import de.pauhull.bansystem.spigot.command.*;
import de.pauhull.bansystem.spigot.listener.PlayerCommandPreprocessListener;
import de.pauhull.bansystem.spigot.listener.PlayerJoinListener;
import de.pauhull.bansystem.spigot.listener.PlayerQuitListener;
import de.pauhull.bansystem.spigot.util.PluginMessageListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotBanSystem extends JavaPlugin {

    @Getter
    private static SpigotBanSystem instance;

    @Getter
    private IReportInventory reportInventory;

    @Override
    public void onEnable() {
        instance = this;

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
        new PluginMessageListener(this);

        if (Bukkit.getVersion().contains("1.8")) {
            new CrashCommand(this);
        }
    }

}
