package de.godtitan.bansystem.bungee.util;

import de.godtitan.bansystem.bungee.BungeeBanSystem;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paul
 * on 28.11.2018
 *
 * @author pauhull
 */
public class Report {

    @Getter
    private Map<String, Reason> reported = new HashMap<>();

    @Getter
    private Map<String, String> reporting = new HashMap<>();

    private BungeeBanSystem plugin;

    public Report(BungeeBanSystem plugin) {
        this.plugin = plugin;
    }

    public boolean isReviewed(String player) {
        return reporting.containsValue(player);
    }

    public boolean isReporting(String player) {
        return reporting.containsKey(player);
    }

    public Reason isReported(String player) {
        return reported.get(player);
    }

    public void setReporting(String reporting, String reported) {
        if (reported != null) {
            this.reporting.put(reporting, reported);
        } else {
            this.reporting.remove(reporting);
        }
    }

    public void setReported(String player, Reason reason) {
        if (reason != null) {
            this.reported.put(player, reason);
        } else {
            this.reported.remove(player);
        }
    }

    public enum Reason {
        HACKING, CHAT, TEAMING, SPAWNTRAPPING, BUGUSING, SKIN, MISC, NAME
    }

}
