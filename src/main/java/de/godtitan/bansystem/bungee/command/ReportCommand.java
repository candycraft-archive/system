package de.godtitan.bansystem.bungee.command;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import de.godtitan.bansystem.bungee.BungeeBanSystem;
import de.godtitan.bansystem.bungee.util.Report;
import de.godtitan.bansystem.common.message.SetReportingMessage;
import de.godtitan.bansystem.common.util.Messages;
import de.godtitan.bansystem.common.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ReportCommand extends Command {

    private BungeeBanSystem plugin;

    public ReportCommand(BungeeBanSystem plugin) {
        super("reportbungee");
        this.plugin = plugin;

        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length > 1 && args[0].equalsIgnoreCase("review")) {

            if (!sender.hasPermission("system.getreport")) {
                sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + Messages.NO_PERMISSIONS));
                return;
            }

            if (args[1].equalsIgnoreCase(player.getName())) {
                player.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Du kannst dich §cnicht §7selbst reporten!"));
                return;
            }

            ProxiedPlayer reported = ProxyServer.getInstance().getPlayer(args[1]);

            if (reported == null) {
                sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + Messages.NOT_ONLINE));
                return;
            }

            if (plugin.getReport().isReporting(player.getName())) {
                sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Du bist bereits in einem §cReport§7!"));
                return;
            }

            if (plugin.getReport().isReported(reported.getName()) == null) {
                sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Dieser Spieler wurde §cnicht §7reportet!"));
                return;
            }

            player.connect(reported.getServer().getInfo());
            SetReportingMessage message = new SetReportingMessage(player.getName(), reported.getName());
            message.sendToServer(reported.getServer().getInfo().getName());

            player.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Du reportest nun §e" + reported.getName()));
            player.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Report-Grund: §e" + plugin.getReport().isReported(reported.getName()).name()));

            plugin.getReport().setReported(reported.getName(), null);
            plugin.getReport().setReporting(player.getName(), reported.getName());

            return;
        } else if (args.length > 2 && args[0].equalsIgnoreCase("player")) {

            ProxiedPlayer reported = ProxyServer.getInstance().getPlayer(args[1]);
            if (reported == null) {
                sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + Messages.NOT_ONLINE));
                return;
            }

            if (args[1].equalsIgnoreCase(player.getName())) {
                sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Du kannst dich §cnicht §7selbst reporten!"));
                return;
            }

            Report.Reason report;
            try {
                report = Report.Reason.valueOf(args[2]);
            } catch (IllegalArgumentException e) {
                report = null;
            }

            if (report == null) {
                player.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§cUngültiger Grund."));
                return;
            }

            plugin.getReport().setReported(reported.getName(), report);
            player.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Du hast §e" + reported.getName() + "§7 für den Grund §e" + report.name() + "§7 reportet!"));

            ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report review " + reported.getName());
            HoverEvent hover = new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Command: /report review " + reported.getName()));
            BaseComponent[] message = new ComponentBuilder("Report bearbeiten").color(ChatColor.YELLOW).bold(true).event(click).event(hover).create();

            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                if (!all.hasPermission("system.getreport"))
                    continue;

                all.sendMessage(TextComponent.fromLegacyText("§8§m          |§e Report §8§m|          "));
                all.sendMessage(TextComponent.fromLegacyText("§7Neuer Report:"));
                all.sendMessage(TextComponent.fromLegacyText(" "));
                all.sendMessage(TextComponent.fromLegacyText("§eReportet von §8» §7" + player.getName()));
                all.sendMessage(TextComponent.fromLegacyText("§eBeschuldigter §8» §7" + reported.getName()));
                all.sendMessage(TextComponent.fromLegacyText("§eGrund §8» §7" + report.name()));
                all.sendMessage(TextComponent.fromLegacyText(" "));
                all.sendMessage(message);
                all.sendMessage(TextComponent.fromLegacyText(" "));
                all.sendMessage(TextComponent.fromLegacyText("§8§m          |§e Report §8§m|          "));
            }

            return;
        } else if (args.length > 0 && args[0].equalsIgnoreCase("quit")) {

            if (plugin.getReport().isReporting(player.getName())) {
                ServerObject server = Util.getRandomFromList(TimoCloudAPI.getUniversalAPI().getServerGroup("Lobby").getServers());
                ServerInfo info = ProxyServer.getInstance().getServerInfo(server.getName());
                player.connect(info);
                plugin.getReport().setReporting(player.getName(), null);
            } else {
                player.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Du bearbeitest gerade §ckeinen Report§7!"));
            }

        }

    }
}
