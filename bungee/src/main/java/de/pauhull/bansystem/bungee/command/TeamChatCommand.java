package de.pauhull.bansystem.bungee.command;

import de.pauhull.bansystem.bungee.BungeeBanSystem;
import de.pauhull.bansystem.common.util.Messages;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;

import java.util.ArrayList;
import java.util.List;

public class TeamChatCommand extends Command implements Listener {

    @Getter
    private static List<String> disabled = new ArrayList<>();

    private BungeeBanSystem plugin;

    public TeamChatCommand(BungeeBanSystem plugin) {
        super("teamchat", null, "tc");
        this.plugin = plugin;

        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("teamchat.use")) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + Messages.NO_PERMISSIONS));
            return;
        }

        if (args.length == 0) {

            sender.sendMessage(TextComponent.fromLegacyText(" "));
            sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "Online:"));

            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (player.hasPermission("teamchat.use")) {
                    sender.sendMessage(TextComponent.fromLegacyText("§8● §a" + player.getName() +
                            " §8(§e" + player.getServer().getInfo().getName() + "§8)"
                            + (disabled.contains(player.getName()) ? " §8[§c§lAusgeloggt§8]" : "")));
                }
            }

            sender.sendMessage(TextComponent.fromLegacyText(" "));

            return;
        }

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + Messages.ONLY_PLAYERS));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
            if (disabled.contains(player.getName())) {
                disabled.remove(player.getName());
                player.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "Du §aerhältst§7 nun Team-Nachrichten!"));
            } else {
                disabled.add(player.getName());
                player.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "Du erhältst nun §ckeine §7Team-Nachrichten mehr!"));
            }

            return;
        }

        if (disabled.contains(player.getName())) {
            player.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "Du kannst keine Team-Nachrichten senden, da du sie §cdeaktiviert §7hast!"));
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                stringBuilder.append(' ');
            }

            stringBuilder.append(args[i]);
        }
        BaseComponent[] message = TextComponent.fromLegacyText("§8[§e§lTEAMCHAT§8] §7" + player.getName() + "§8 » §f" + stringBuilder.toString());

        for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
            if (all.hasPermission("teamchat.use") && !disabled.contains(all.getName())) {
                all.sendMessage(message);
            }
        }
    }
}
