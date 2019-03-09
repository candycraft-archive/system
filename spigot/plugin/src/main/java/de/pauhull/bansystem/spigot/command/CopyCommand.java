package de.pauhull.bansystem.spigot.command;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import de.pauhull.bansystem.common.util.Messages;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Created by Paul
 * on 02.12.2018
 *
 * @author pauhull
 */
public class CopyCommand implements CommandExecutor {

    public CopyCommand(JavaPlugin plugin) {
        plugin.getCommand("copy").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("system.copy")) {
            commandSender.sendMessage(Messages.PREFIX + Messages.NO_PERMISSIONS);
            return true;
        }

        ServerGroupObject group = TimoCloudAPI.getBukkitAPI().getThisServer().getGroup();

        if (group.isStatic()) {
            commandSender.sendMessage(Messages.PREFIX + "Diese Gruppe ist §cstatic§7!");
            return true;
        }

        File currentServer = new File("").getAbsoluteFile();
        File copyTo = new File("/home/timocloud/core/templates/server/" + group.getName());
        File global = new File("/home/timocloud/core/templates/server/Global");

        commandSender.sendMessage("§7Speichere Server...");
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "save-all");
        commandSender.sendMessage(String.format("§7Lösche \"%s\"", copyTo.getPath()));
        if (copyTo.exists()) copyTo.delete();
        commandSender.sendMessage(String.format("§7Kopiere \"%s\" nach \"%s\"...", currentServer.getPath(), copyTo.getPath()));

        try {

            FileUtils.copyDirectory(currentServer, copyTo, file -> {

                if (!file.isDirectory()) {
                    File fileInGlobal = new File(global.getPath() + file.getPath().replace(currentServer.getPath(), ""));

                    return !fileInGlobal.exists();
                }

                return true;

            }, true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        commandSender.sendMessage("§7Entferne leere Ordner...");

        for (File file : FileUtils.listFilesAndDirs(copyTo, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
            if (file.isDirectory() && file.list().length == 0) {
                file.delete();
            }
        }

        commandSender.sendMessage("§aFertig!");

        return true;
    }

}
