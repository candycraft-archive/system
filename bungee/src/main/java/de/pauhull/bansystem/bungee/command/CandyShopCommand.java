package de.pauhull.bansystem.bungee.command;

import de.pauhull.bansystem.bungee.BungeeBanSystem;
import de.pauhull.bansystem.common.util.Messages;
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
public class CandyShopCommand extends Command {

    private BungeeBanSystem plugin;

    public CandyShopCommand(BungeeBanSystem plugin) {
        super("candyshop", null, "cs");
        this.plugin = plugin;

        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "Unser Shop: §e§nhttp://shop.candycraft.de/"));
    }

}
