package de.godtitan.bansystem.bungee.command;

import de.godtitan.bansystem.bungee.BungeeBanSystem;
import de.godtitan.bansystem.common.util.BanInfo;
import de.godtitan.bansystem.common.util.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BanLogCommand extends Command {

    private BungeeBanSystem plugin;

    public BanLogCommand(BungeeBanSystem plugin) {
        super("banlog");
        this.plugin = plugin;

        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender.hasPermission("system.getbanreasons"))) {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + Messages.NO_PERMISSIONS));
            return;
        }

        if (args.length == 1) {

            String requestedPlayerName = args[0];
            plugin.getUuidFetcher().fetchUUIDAsync(requestedPlayerName, uuid -> {

                if (uuid != null) {
                    plugin.getBanLogTable().getLogs(uuid, logs -> {

                        if (logs.isEmpty()) {
                            plugin.getUuidFetcher().fetchNameAsync(uuid, name -> {
                                sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§e" + name + " §7wurde noch §cnie §7gebannt!"));
                            });
                        } else {
                            plugin.getUuidFetcher().fetchNameAsync(uuid, name -> {
                                BungeeBanSystem.getInstance().getBanTable().getBan(uuid, currentBan -> {
                                    sender.sendMessage(TextComponent.fromLegacyText("§8§m                            "));
                                    sender.sendMessage(TextComponent.fromLegacyText(" "));
                                    sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Alle geloggten Bans von §e" + name + "§7:"));
                                    sender.sendMessage(TextComponent.fromLegacyText(" "));

                                    int i = 0;
                                    for (BanInfo banInfo : logs) {
                                        if (i++ > 0) {
                                            sender.sendMessage(TextComponent.fromLegacyText("§8§m                            "));
                                        }

                                        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                                        Date bannedOn = new Date(banInfo.getTime());
                                        long expiresOnTime = banInfo.getTime() + banInfo.getReason().getTime();

                                        String expires;
                                        if (expiresOnTime != -1) {
                                            expires = format.format(new Date(expiresOnTime));
                                        } else {
                                            expires = "§c§lNIE";
                                        }

                                        plugin.getUuidFetcher().fetchNameAsync(banInfo.getBannedBy(), bannedByName -> {

                                            sender.sendMessage(TextComponent.fromLegacyText("§eGrund §8» §7" + banInfo.getReason().getReason()));

                                            if (expiresOnTime != -1 && System.currentTimeMillis() >= expiresOnTime) {
                                                sender.sendMessage(TextComponent.fromLegacyText("§eStatus §8» §7Abgelaufen"));
                                            } else if (currentBan != null && currentBan.equals(banInfo)) {
                                                sender.sendMessage(TextComponent.fromLegacyText("§eStatus §8» §7Entbannt"));
                                            } else {
                                                sender.sendMessage(TextComponent.fromLegacyText("§eStatus §8» §7Inaktiv"));
                                            }

                                            sender.sendMessage(TextComponent.fromLegacyText("§eGebannt von §8» §7" + bannedByName));
                                            sender.sendMessage(TextComponent.fromLegacyText("§eGebannt am §8» §7" + format.format(bannedOn)));
                                            sender.sendMessage(TextComponent.fromLegacyText("§eLäuft ab §8» §7" + expires));
                                            sender.sendMessage(TextComponent.fromLegacyText("§eIP-Adresse §8» §7" + banInfo.getAddress().getHostAddress()));
                                        });

                                    }
                                    sender.sendMessage(TextComponent.fromLegacyText("§8§m                            "));
                                    sender.sendMessage(TextComponent.fromLegacyText(" "));

                                });
                            });
                        }

                    });
                } else {
                    sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "Dieser Spieler §cexistiert§7 nicht!"));
                }

            });
        } else {
            sender.sendMessage(TextComponent.fromLegacyText(Messages.BAN_PREFIX + "§c/banlog <Spieler>"));
        }
    }

}
