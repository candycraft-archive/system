package de.godtitan.bansystem.bungee.listener;

import de.godtitan.bansystem.bungee.BungeeBanSystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class LoginListener implements Listener {

    private BungeeBanSystem plugin;

    public LoginListener(BungeeBanSystem plugin) {
        this.plugin = plugin;

        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onLogin(LoginEvent event) {

        event.registerIntent(plugin);

        plugin.getUuidFetcher().fetchUUIDAsync(event.getConnection().getName(), uuid -> {

            if (uuid == null) {
                event.completeIntent(plugin);
                return;
            }

            plugin.getBanTable().getBan(uuid, event.getConnection().getAddress().getAddress(), ban -> {
                if (ban != null) {

                    long remaining = ban.getEnd() - System.currentTimeMillis();

                    if (ban.getEnd() != -1 && remaining <= 0) {
                        plugin.getBanTable().unban(uuid);
                        event.completeIntent(plugin);
                        return;
                    }

                    String endString = "§c§lPERMANENT";

                    if (ban.getEnd() != -1) {

                        long days = (long) Math.floor((double) remaining / (double) TimeUnit.DAYS.toMillis(1));
                        remaining = remaining % TimeUnit.DAYS.toMillis(1);
                        long hours = (long) Math.floor((double) remaining / (double) TimeUnit.HOURS.toMillis(1));
                        remaining = remaining % TimeUnit.HOURS.toMillis(1);
                        long minutes = (long) Math.floor((double) remaining / (double) TimeUnit.MINUTES.toMillis(1));

                        endString = "Noch §e" + days + "§7 Tag" + (days != 1 ? "e" : "") + ", §e" + hours + "§7 Stunde" + (hours != 1 ? "n" : "") + ", §e" + minutes + "§7 Minute" + (minutes != 1 ? "n" : "");
                    }

                    event.setCancelReason("§d§lCandyCraft§7.§dde\n§7Du wurdest vom Server §cgebannt§7!\n" +
                            "\n§8§m                                                \n\n§eGrund §8» §c" + ban.getReason().getReason().split(" ")[0] + "\n§7"
                            + endString + "\n\n§7Du kannst auf §e§nforum.candycraft.de§r§7 einen Entbannungsantrag stellen.");
                    event.setCancelled(true);

                    event.completeIntent(plugin);
                } else {
                    event.completeIntent(plugin);
                }

            });

        });

    }
}
