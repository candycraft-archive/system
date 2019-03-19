package de.pauhull.bansystem.bungee.util;

import me.philipsnostrum.bungeepexbridge.BungeePexBridge;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Paul
 * on 16.03.2019
 *
 * @author pauhull
 */
public class PermissionHelper {

    public static List<String> getPermissions(UUID uuid) throws Exception {
        List<String> permissions = new ArrayList<>();
        ProxiedPlayer player = new FakePlayer(uuid);
        for (String group : BungeePexBridge.getPerms().getPlayerGroups(player)) {
            permissions.addAll(getGroupPermissions(group));
        }
        permissions.addAll(BungeePexBridge.getPerms().getPlayerPermissions(player));
        return permissions;
    }

    public static List<String> getGroupPermissions(String group) throws Exception {
        return getGroupPermissions(group, new ArrayList<>());
    }

    public static List<String> getGroupPermissions(String group, List<String> permissions) throws Exception {
        if (group == null) {
            return permissions;
        }

        permissions.addAll(BungeePexBridge.getPerms().getGroupPermissions(group));
        List<String> inheritances = BungeePexBridge.getPerms().getInheritance(group);
        if (inheritances != null) {
            for (String inheritance : inheritances) {
                getGroupPermissions(inheritance, permissions);
            }
        }

        return permissions;
    }

}
