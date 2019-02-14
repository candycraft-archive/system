package de.godtitan.bansystem.bungee.command;

import de.godtitan.bansystem.bungee.BungeeBanSystem;
import de.godtitan.bansystem.common.util.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Paul
 * on 01.01.2019
 *
 * @author pauhull
 */
public class YoutubeCommand extends Command {

    public YoutubeCommand(BungeeBanSystem plugin) {
        super("youtube", null, "yt", "youtuber");
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "Hier siehst du die Anforderungen für den Youtuber-Rang: §e§nhier link einfügen"));
    }

}
