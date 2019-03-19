package de.pauhull.bansystem.bungee.listener;

import de.pauhull.bansystem.bungee.BungeeBanSystem;
import de.pauhull.bansystem.bungee.command.TeamChatCommand;
import de.pauhull.bansystem.bungee.util.MessageInfo;
import de.pauhull.bansystem.common.util.Messages;
import de.pauhull.bansystem.common.util.MuteInfo;
import de.pauhull.bansystem.common.util.TimedHashMap;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Paul
 * on 01.01.2019
 *
 * @author pauhull
 */
public class ChatListener implements Listener {

    private Map<ProxiedPlayer, MessageInfo> lastMessages;
    private List<String> domainEndings;
    private List<String> badWords;
    private BungeeBanSystem plugin;
    private NormalizedLevenshtein stringComparator;

    public ChatListener(BungeeBanSystem plugin) {
        this.plugin = plugin;
        this.lastMessages = new TimedHashMap<>(TimeUnit.MINUTES, 1);
        this.badWords = loadLinesFromUrl("https://candycraft.de/mute/badwords.txt");
        this.domainEndings = loadLinesFromUrl("https://candycraft.de/mute/domain-endings.txt");
        this.stringComparator = new NormalizedLevenshtein();
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer))
            return;
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        if (event.isCommand()) {
            if (!isCommand(event.getMessage(), "gmsg", "gr", "greply", "msg", "tc", "teamchat", "pc", "partychat", "tell", "r", "reply")) {
                return;
            }
        }

        if (lastMessages.containsKey(player)) {
            if (stringComparator.similarity(lastMessages.get(player).getMessage().toLowerCase(), event.getMessage().toLowerCase()) >= 0.6) {
                player.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§cDeine Nachricht ähnelt der vorherigen zu sehr."));
                event.setCancelled(true);
                return;
            }
            if (System.currentTimeMillis() - lastMessages.get(player).getTimestamp() < 1000) {
                player.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§cBitte warte etwas, bis du wieder eine Nachricht sendest."));
                event.setCancelled(true);
                return;
            }
        }

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

                player.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§cDein Chatlog: §ehttps://candycraft.de/mute/?id=" + mute.getMuteId()));
                event.setCancelled(true);
                return;
            } else {
                iterator.remove();
                plugin.getMuteTable().unmute(player.getUniqueId());
                return;
            }
        }

        String messageFiltered = event.getMessage().toLowerCase()
                .replace("_", "")
                .replace(",", "")
                .replace("-", "")
                .replace(" ", "")
                .replace("(.)", ".")
                .replace("[.]", ".")
                .replace("<.>", ".")
                .replace("(punkt)", ".")
                .replace("[punkt]", ".")
                .replace("<punkt>", ".")
                .replace("(point)", ".")
                .replace("[point]", ".")
                .replace("<point>", ".");

        boolean blocked = false;

        for (String domainEnding : domainEndings) {
            if (messageFiltered.contains(domainEnding)) {
                blocked = true;
                break;
            }
        }

        messageFiltered = messageFiltered.replace(".", "");

        if (!blocked) {
            for (String badWord : badWords) {
                if (messageFiltered.contains(badWord)) {
                    blocked = true;
                    break;
                }
            }
        }

        if (blocked) {
            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                BaseComponent[] message = new ComponentBuilder(Messages.BAN_PREFIX).append("[Spieler Muten]").color(ChatColor.YELLOW).bold(true).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mute " + player.getName() + " ")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§8Command: /mute " + player.getName() + " "))).create();
                if (all.hasPermission("teamchat.use") && !TeamChatCommand.getDisabled().contains(all.getName())) {
                    all.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§cVerdächtige Nachricht von §4" + player.getName() + "§c: §r" + event.getMessage()));
                    all.sendMessage(message);
                }
            }
        }

        lastMessages.put(player, new MessageInfo(event.getMessage(), System.currentTimeMillis()));
        plugin.getChatLogTable().log(player.getUniqueId(), player.getName(), event.getMessage());
    }

    private boolean isCommand(String s, String... available) {
        boolean success = false;
        for (String check : available) {
            if (s.toLowerCase().startsWith("/" + check + " ")) {
                success = true;
            }
        }
        return success;
    }

    private List<String> loadLinesFromUrl(String urlString) {
        List<String> list = new ArrayList<>();
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
            reader.close();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

}
