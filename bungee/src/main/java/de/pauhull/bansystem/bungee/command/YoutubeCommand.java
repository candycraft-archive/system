package de.pauhull.bansystem.bungee.command;

import de.pauhull.bansystem.bungee.BungeeBanSystem;
import de.pauhull.bansystem.common.util.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Paul
 * on 01.01.2019
 *
 * @author pauhull
 */
public class YoutubeCommand extends Command {

    private static BaseComponent[] message = new ComponentBuilder(Messages.PREFIX)
            .append("Hier siehst du die Anforderungen für den Youtuber-Rang: ").color(ChatColor.GRAY)
            .append("*KLICK*").color(ChatColor.YELLOW).underlined(true).event(new ClickEvent(Action.OPEN_URL, "https://forum.candycraft.de/index.php?thread/107-anforderungen-f%C3%BCr-den-youtuber-rang/"))
            .create();

    public YoutubeCommand(BungeeBanSystem plugin) {
        super("youtube", null, "yt", "youtuber");
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        sender.sendMessage(message);
    }

}
