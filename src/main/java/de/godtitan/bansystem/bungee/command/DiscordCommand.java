package de.godtitan.bansystem.bungee.command;

import de.godtitan.bansystem.bungee.BungeeBanSystem;
import de.godtitan.bansystem.common.util.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Paul
 * on 17.02.2019
 *
 * @author pauhull
 */
public class DiscordCommand extends Command {

    private BungeeBanSystem plugin;

    public DiscordCommand(BungeeBanSystem plugin) {
        super("discord");
        this.plugin = plugin;

        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "Unser Discord: §e§nhttps://discord.gg/gae5E7J"));
    }

}
