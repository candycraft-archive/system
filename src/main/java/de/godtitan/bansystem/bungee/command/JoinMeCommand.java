package de.godtitan.bansystem.bungee.command;

import de.godtitan.bansystem.bungee.BungeeBanSystem;
import de.godtitan.bansystem.common.util.ChatFace;
import de.godtitan.bansystem.common.util.Messages;
import de.pauhull.uuidfetcher.common.fetcher.TimedHashMap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.concurrent.TimeUnit;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class JoinMeCommand extends Command {

    private ChatFace chatFace;
    private TimedHashMap<String, Object> requested = new TimedHashMap<>(TimeUnit.MINUTES, 3);

    public JoinMeCommand(BungeeBanSystem plugin) {
        super("joinme");
        this.chatFace = new ChatFace(plugin.getExecutorService());

        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + Messages.ONLY_PLAYERS));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length == 0) {

            if (player.hasPermission("system.joinme")) {

                if (requested.containsKey(player.getName())) {
                    player.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "Bitte §cwarte§7, bevor du wieder eine Nachjoin-Nachricht senden kannst."));
                    return;
                }

                chatFace.getLinesAsync(player.getName(), lines -> {
                    ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(" "));
                    ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText("§6§l§k-----------------------------------"));

                    for (String line : lines) {
                        ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(line));
                    }

                    ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(" "));
                    ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText("§e§l" + player.getName() + "§a spielt nun auf §2" + player.getServer().getInfo().getName()));

                    HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Command: /joinme " + player.getName()));
                    ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/joinme " + player.getName());
                    BaseComponent[] message = new ComponentBuilder("Klicke hier, um nachzujoinen").color(ChatColor.AQUA).bold(true).event(hover).event(click).create();
                    ProxyServer.getInstance().broadcast(message);

                    ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText("§6§l§k-----------------------------------"));
                    ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(" "));

                    requested.put(player.getName(), null);
                });
            } else {
                player.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + Messages.NO_PERMISSIONS));
            }

        } else {
            ProxiedPlayer toJoin = ProxyServer.getInstance().getPlayer(args[0]);

            if (toJoin == null) {
                player.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + Messages.NOT_ONLINE));
            } else {
                if (requested.containsKey(toJoin.getName())) {
                    ServerInfo info = toJoin.getServer().getInfo();
                    player.connect(info);
                } else {
                    player.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "§e" + toJoin.getName() + "§7 hat in den letzten 3 Minuten §ckeine §7Nachjoin-Anfrage gesendet!"));
                }
            }
        }

    }

}
