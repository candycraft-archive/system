package de.godtitan.bansystem.bungee.command;

import de.godtitan.bansystem.bungee.BungeeBanSystem;
import de.godtitan.bansystem.common.util.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PlaytimeCommand extends Command {

    private BungeeBanSystem plugin;

    public PlaytimeCommand(BungeeBanSystem plugin) {
        super("playtime");
        this.plugin = plugin;

        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (args.length == 1) {
                plugin.getUuidFetcher().fetchUUIDAsync(args[0], uuid -> {
                    if (uuid != null) {
                        plugin.getExecutorService().execute(() -> {
                            long l = plugin.getPlaytimeTable().getPlaytime(uuid);
                            if (l == -1) {
                                sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "Der Spieler war noch §enie§7 online!"));
                            }else {
                                sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "Der Spieler war schon §e"+ getString(l) +"§7 online!"));
                            }
                        });
                    }else {
                        player.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "Der Spieler existiert noch nicht!"));
                    }
                });
            }else {
                plugin.getExecutorService().execute(() -> {
                    long l = plugin.getPlaytimeTable().getPlaytime(((ProxiedPlayer) sender).getUniqueId());
                    sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "Du warst schon §e"+ getString(l) +"§7 online!"));
                });
            }
        }
    }
    private String getString(long l) {
        String s = "";
        long millis = l;
        int seconds = 0;
        int minutes = 0;
        int hours = 0;
        int days = 0;
        while (millis >= 1000) {
            millis = millis -1000;
            seconds ++;
        }
        while (seconds >= 60) {
            seconds = seconds -60;
            minutes ++;
        }
        while (minutes >= 60) {
            minutes = minutes -60;
            hours ++;
        }
        while (hours >= 24) {
            hours = hours - 24;
            days++;
        }
        if (days != 0) {
            if(days == 1) {
                s = s + days + " Tag";
            }else {
                s = s + days + " Tage";
            }
        }
        if(hours != 0) {
            if (hours == 1) {
                s = s + ", 1 Stunde";
            }else {
                s = s + ", " + hours + " Stunden";
            }
        }
        if(minutes != 0) {
            if (minutes == 1) {
                s = s + ", 1 Minute";
            }else {
                s = s + ", " + minutes + " Minuten";
            }
        }
        if(seconds != 0) {
            if (seconds == 1) {
                s = s + ", 1 Sekunde";
            }else {
                s = s + ", " + seconds + " Sekunden";
            }
        }
        if(s.startsWith(", ")) {
            s = s.substring(2, s.length());
        }
        return s;
    }
}
