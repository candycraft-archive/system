package de.godtitan.bansystem.spigot.util;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.messages.listeners.MessageListener;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import de.godtitan.bansystem.common.message.SetReportingMessage;
import de.godtitan.bansystem.spigot.SpigotBanSystem;
import de.godtitan.bansystem.spigot.listener.PlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by Paul
 * on 30.11.2018
 *
 * @author pauhull
 */
public class PluginMessageListener implements MessageListener {

    private SpigotBanSystem plugin;

    public PluginMessageListener(SpigotBanSystem plugin) {
        this.plugin = plugin;

        TimoCloudAPI.getMessageAPI().registerMessageListener(this);
    }

    @Override
    public void onPluginMessage(AddressedPluginMessage addressedPluginMessage) {

        if (addressedPluginMessage.getMessage().getType().equals(SetReportingMessage.TYPE)) {
            SetReportingMessage message = new SetReportingMessage(addressedPluginMessage.getMessage());

            Player reporting = Bukkit.getPlayer(message.getReporting());
            Player reported = Bukkit.getPlayer(message.getReported());

            if (reporting != null) {
                PlayerJoinListener.report(reporting, reported);
            } else {
                PlayerJoinListener.getReport().put(message.getReporting(), message.getReported());
            }
        }
    }

}
