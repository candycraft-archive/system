package de.pauhull.bansystem.bungee;

import de.pauhull.bansystem.bungee.command.*;
import de.pauhull.bansystem.bungee.listener.*;
import de.pauhull.bansystem.bungee.util.Report;
import de.pauhull.bansystem.common.data.MySQL;
import de.pauhull.bansystem.common.data.table.*;
import de.pauhull.bansystem.common.util.MuteInfo;
import de.pauhull.uuidfetcher.bungee.BungeeUUIDFetcher;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BungeeBanSystem extends Plugin implements Listener {

    @Getter
    private static BungeeBanSystem instance;

    @Getter
    private List<MuteInfo> mutes = new ArrayList<>();

    @Getter
    private MySQL mySQL;

    @Getter
    private ExecutorService executorService;

    @Getter
    private Configuration config;

    @Getter
    private PlaytimeTable playtimeTable;

    @Getter
    private BanTable banTable;

    @Getter
    private MuteTable muteTable;

    @Getter
    private BanLogTable banLogTable;

    @Getter
    private ChatLogTable chatLogTable;

    @Getter
    private BungeeUUIDFetcher uuidFetcher;

    @Getter
    private Report report;

    @Getter
    private boolean maintenance;

    @Getter
    private String maintenancePermission;

    @Getter
    private String maintenanceMessage;

    @Override
    public void onEnable() {
        instance = this;

        this.report = new Report(this);
        this.uuidFetcher = new BungeeUUIDFetcher();
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2);
        this.config = copyAndLoad("config.yml", new File(getDataFolder(), "config.yml"));

        if (config != null) {
            this.maintenance = config.getBoolean("Maintenance");
            this.maintenancePermission = config.getString("Permission");
            this.maintenanceMessage = ChatColor.translateAlternateColorCodes('&', config.getString("Message"));

            this.mySQL = new MySQL(config.getString("MySQL.Host"),
                    config.getString("MySQL.Port"),
                    config.getString("MySQL.Database"),
                    config.getString("MySQL.User"),
                    config.getString("MySQL.Password"),
                    config.getBoolean("MySQL.SSL"));
        }

        if (!mySQL.connect()) {
            ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText("§cMySQL konnte nicht geladen werden! Plugin wird heruntergefahren..."));
            this.executorService.shutdown();
            return;
        }

        this.muteTable = new MuteTable(mySQL, executorService);
        this.banLogTable = new BanLogTable(mySQL, executorService);
        this.banTable = new BanTable(mySQL, executorService, banLogTable);
        this.playtimeTable = new PlaytimeTable(mySQL, executorService);
        this.chatLogTable = new ChatLogTable(mySQL, executorService);

        new BanLogCommand(this);
        new BroadcastCommand(this);
        new BanCommand(this);
        new ForumCommand(this);
        new JoinMeCommand(this);
        new PingCommand(this);
        new ReportCommand(this);
        new TeamChatCommand(this);
        new TsCommand(this);
        new UnmuteCommand(this);
        new MuteCommand(this);
        new KickCommand(this);
        new YoutubeCommand(this);
        new UnbanCommand(this);
        new CandyShopCommand(this);
        new DiscordCommand(this);
        new GotoCommand(this);
        new PlaytimeCommand(this);
        new MaintenanceCommand(this);
        new MuteInfoCommand(this);

        new ChatListener(this);
        new ProxyPingListener(this);
        new PlayerDisconnectListener(this);
        new LoginListener(this);
        new PostLoginListener(this);
        new ServerKickListener(this);

        muteTable.getAllMutes(mutes -> {
            this.mutes.addAll(mutes);
        });

        String[] messages = new String[]{
                "§dDu möchtest Coole Ränge?\n" +
                        "&6Dann schau doch Mal in unserem &cShop &6vorbei &7- &c/candyshop",
                "&6Wir haben auch einen §cTeamspeak &7- &c/teamspeak",
                "&6Schau doch Mal in unserem &cForum &6vorbei &7- &c/forum",
                "&6Wir haben auch einen §cDiscord Server &7- &c/discord",
                "&6Wir suchen derzeit noch &9Supporter &7- &c/forum"
        };

        AtomicInteger index = new AtomicInteger(0);
        ProxyServer.getInstance().getScheduler().schedule(this, () -> {
            String message = ChatColor.translateAlternateColorCodes('&', messages[index.getAndIncrement() % messages.length]);
            ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(" \n§e§lInfo §7»§r\n" + message + "\n "));
        }, 0, 300, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        this.executorService.shutdown();
        this.mySQL.close();
    }

    private Configuration copyAndLoad(String resource, File file) {

        if (!file.exists()) {
            file.getParentFile().mkdirs();

            try {
                Files.copy(getResourceAsStream(resource), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
        this.config.set("Maintenance", maintenance);
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "config,yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
