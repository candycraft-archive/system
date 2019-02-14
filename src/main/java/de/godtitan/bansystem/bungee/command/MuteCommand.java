package de.godtitan.bansystem.bungee.command;

import de.godtitan.bansystem.bungee.BungeeBanSystem;
import de.godtitan.bansystem.common.util.HashGenerator;
import de.godtitan.bansystem.common.util.Messages;
import de.godtitan.bansystem.common.util.MuteInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Paul
 * on 01.01.2019
 *
 * @author pauhull
 */
public class MuteCommand extends Command {

    private BungeeBanSystem plugin;

    public MuteCommand(BungeeBanSystem plugin) {
        super("mute");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("system.mute")) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + Messages.NO_PERMISSIONS));
            return;
        }

        if (args.length != 1 && args.length != 3) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§c/mute <Spieler> [Zeit] [s|min|h|d]"));
            return;
        }

        if (args[0].equalsIgnoreCase(sender.getName())) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Du kannst dich §cnicht §7selbst muten!"));
            return;
        }

        plugin.getUuidFetcher().fetchProfileAsync(args[0], (name, uuid) -> {
            if (uuid == null) {
                sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Dieser Spieler existiert §cnicht§7!"));
                return;
            }

            long time = -1;
            if (args.length == 3) {
                int duration;
                try {
                    duration = Integer.parseInt(args[1]);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§c/mute <Spieler> [Zeit] [s|min|h|d]"));
                    return;
                }

                TimeUnit unit = null;
                if (args[2].equalsIgnoreCase("s")) {
                    unit = TimeUnit.SECONDS;
                } else if (args[2].equalsIgnoreCase("min")) {
                    unit = TimeUnit.MINUTES;
                } else if (args[2].equalsIgnoreCase("h")) {
                    unit = TimeUnit.HOURS;
                } else if (args[2].equalsIgnoreCase("d")) {
                    unit = TimeUnit.DAYS;
                }

                if (unit == null) {
                    sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§c/mute <Spieler> [Zeit] [s|min|h|d]"));
                    return;
                }

                time = System.currentTimeMillis() + unit.toMillis(duration);
            }

            UUID mutedBy = UUID.fromString("f78a4d8d-d51b-4b39-98a3-230f2de0c670");
            if (sender instanceof ProxiedPlayer) {
                mutedBy = ((ProxiedPlayer) sender).getUniqueId();
            }

            final UUID finalMutedBy = mutedBy;
            final long finalTime = time;
            plugin.getMutes().removeIf(muteInfo -> muteInfo.getUuid().equals(uuid));
            plugin.getMuteTable().mute(uuid, mutedBy, time, (muteId, secretKey) -> {
                MuteInfo info = new MuteInfo(uuid, muteId, finalTime, finalMutedBy);
                plugin.getMutes().add(info);
                sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§cGeheimer Schlüssel: §e" + secretKey + "§8(md5: " + HashGenerator.generateHexMd5Hash(secretKey)));
            });

            String selfMessage = Messages.BAN_PREFIX + "§cDu wurdest";
            String message = Messages.BAN_PREFIX + "§e" + sender.getName() + "§c hat §e" + name;
            if (time == -1) {
                message += "§4 PERMANENT";
                selfMessage += "§4 PERMANENT";
            } else {
                long remaining = time - System.currentTimeMillis();
                long days = (long) Math.floor((double) remaining / (double) TimeUnit.DAYS.toMillis(1));
                remaining = remaining % TimeUnit.DAYS.toMillis(1);
                long hours = (long) Math.floor((double) remaining / (double) TimeUnit.HOURS.toMillis(1));
                remaining = remaining % TimeUnit.HOURS.toMillis(1);
                long minutes = (long) Math.floor((double) remaining / (double) TimeUnit.MINUTES.toMillis(1));

                String addition = "§c für §e" + days + "§c Tag" + (days != 1 ? "e" : "") + ", §e" + hours + "§c Stunde" + (hours != 1 ? "n" : "") + ", §e" + minutes + "§c Minute" + (minutes != 1 ? "n" : "");
                message += addition;
                selfMessage += addition;
            }
            message += "§c gemutet!";
            selfMessage += "§c gemutet!";

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
            if (player != null) {
                player.sendMessage(TextComponent.fromLegacyText(selfMessage));
            }

            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                if (all.getUniqueId().equals(mutedBy)
                        || (all.hasPermission("teamchat.use") && !TeamChatCommand.getDisabled().contains(all.getName()))) {

                    all.sendMessage(TextComponent.fromLegacyText(message));
                }
            }
        });
    }

}
