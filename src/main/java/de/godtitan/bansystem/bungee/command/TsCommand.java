package de.godtitan.bansystem.bungee.command;

import de.godtitan.bansystem.bungee.BungeeBanSystem;
import de.godtitan.bansystem.common.util.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class TsCommand extends Command {

    private BungeeBanSystem plugin;

    public TsCommand(BungeeBanSystem plugin) {
        super("teamspeak", null, "ts");
        this.plugin = plugin;

        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "Unser Teamspeak ist unter §ecandycraft.de §7zu finden!"));
    }

}
