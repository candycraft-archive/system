package de.pauhull.bansystem.spigot.command;

import de.pauhull.bansystem.common.util.Messages;
import de.pauhull.bansystem.spigot.SpigotBanSystem;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

/**
 * Created by Paul
 * on 06.01.2019
 *
 * @author pauhull
 */
public class CrashCommand implements CommandExecutor {

    private SpigotBanSystem plugin;

    public CrashCommand(SpigotBanSystem plugin) {
        this.plugin = plugin;
        plugin.getCommand("crash").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("system.crash")) {
            sender.sendMessage(Messages.BAN_PREFIX + Messages.NO_PERMISSIONS);
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Messages.BAN_PREFIX + "§c/crash <Spieler>");
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(Messages.BAN_PREFIX + Messages.NOT_ONLINE);
            return true;
        }

        PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) player).getHandle());
        DataWatcher watcher = new DataWatcher(null);
        watcher.a(6, (byte) 20);
        setValue(packet, "i", watcher);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        sender.sendMessage("§aSpieler efolgreich gecrasht ;)");

        return true;
    }

    private void setValue(Object obj, String fieldName, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}
