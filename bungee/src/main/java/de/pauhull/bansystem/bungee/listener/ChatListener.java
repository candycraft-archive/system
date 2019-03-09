package de.pauhull.bansystem.bungee.listener;

import de.pauhull.bansystem.bungee.BungeeBanSystem;
import de.pauhull.bansystem.bungee.command.TeamChatCommand;
import de.pauhull.bansystem.common.util.Messages;
import de.pauhull.bansystem.common.util.MuteInfo;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Paul
 * on 01.01.2019
 *
 * @author pauhull
 */
public class ChatListener implements Listener {

    private Map<String, String> replacements = new HashMap<>();
    private List<String> badWords = new ArrayList<>();

    private BungeeBanSystem plugin;

    public ChatListener(BungeeBanSystem plugin) {
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(plugin.getResourceAsStream("badwords.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(";")) {
                    replacements.put(line.split(";")[0], line.split(";")[1]);
                } else {
                    badWords.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer))
            return;

        if (event.isCommand())
            return;

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        Iterator<MuteInfo> iterator = plugin.getMutes().iterator();
        while (iterator.hasNext()) {
            MuteInfo mute = iterator.next();
            if (!mute.getUuid().equals(player.getUniqueId())) {
                continue;
            }
            if (mute.getTime() == -1 || System.currentTimeMillis() < mute.getTime()) {
                player.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§cDu bist gemutet und kannst keine Nachrichten senden."));

                if (mute.getTime() != -1) {
                    long remaining = mute.getTime() - System.currentTimeMillis();
                    long days = (long) Math.floor((double) remaining / (double) TimeUnit.DAYS.toMillis(1));
                    remaining = remaining % TimeUnit.DAYS.toMillis(1);
                    long hours = (long) Math.floor((double) remaining / (double) TimeUnit.HOURS.toMillis(1));
                    remaining = remaining % TimeUnit.HOURS.toMillis(1);
                    long minutes = (long) Math.floor((double) remaining / (double) TimeUnit.MINUTES.toMillis(1));

                    player.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§cRestzeit: §e" + days + "§c Tag" + (days != 1 ? "e" : "") + ", §e" + hours + "§c Stunde" + (hours != 1 ? "n" : "") + ", §e" + minutes + "§c Minute" + (minutes != 1 ? "n" : "")));
                } else {
                    player.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§cRestzeit: §4PERMANENT"));
                }

                player.sendMessage(Messages.BAN_PREFIX + "§cDeine Mute-ID: §e" + mute.getMuteId());
                event.setCancelled(true);
                return;
            } else {
                iterator.remove();
                plugin.getMuteTable().unmute(player.getUniqueId());
                return;
            }
        }

        String messageFiltered = event.getMessage().toLowerCase()
                .replace(".", "")
                .replace("_", "")
                .replace(",", "")
                .replace("-", "")
                .replace(" ", "");

        for (String badWord : badWords) {
            if (messageFiltered.contains(badWord.toLowerCase())) {
                for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                    if (all.hasPermission("teamchat.use") && !TeamChatCommand.getDisabled().contains(all.getName())) {
                        all.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§4" + player.getName() + "§c hat versucht, folgende Nachricht zu senden: §r" + event.getMessage()));
                    }
                }
                player.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§cDeine Nachricht wurde blockiert und dem Team gemeldet."));
                event.setCancelled(true);
                return;
            }
        }
        /*
        String message = event.getMessage();
        for (String key : replacements.keySet()) {
            message = message.replaceAll("(?i)" + key, replacements.get(key));
        }
        event.setMessage(message);*/
    }

}
