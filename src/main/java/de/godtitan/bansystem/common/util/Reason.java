package de.godtitan.bansystem.common.util;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * Created by Paul
 * on 27.11.2018
 *
 * @author pauhull
 */
public enum Reason {

    HACKING("Hacking", TimeUnit.DAYS.toMillis(30), 1),
    CHAT("Chatverhalten", TimeUnit.DAYS.toMillis(2), 2),
    TEAMING("Teaming", TimeUnit.DAYS.toMillis(3), 3),
    SPAWN_TRAPPING("Spawntrapping", TimeUnit.DAYS.toMillis(3), 4),
    BUGUSING("Bugusing", TimeUnit.DAYS.toMillis(30), 5),
    NAME("Name", TimeUnit.DAYS.toMillis(15), 6),
    SKIN("Skin", TimeUnit.DAYS.toMillis(3), 7),
    MISC_30_MIN("Sonstiges (30 Minuten)", TimeUnit.MINUTES.toMillis(30), 8),
    MISC_2_HRS("Sonstiges (2 Stunden)", TimeUnit.HOURS.toMillis(2), 9),
    MISC_6_HRS("Sonstiges (6 Stunden)", TimeUnit.HOURS.toMillis(6), 10),
    MISC_2_DAYS("Sonstiges (2 Tage)", TimeUnit.DAYS.toMillis(2), 11),
    MISC_15_DAYS("Sonstiges (15 Tage)", TimeUnit.DAYS.toMillis(15), 12),
    MISC_30_DAYS("Sonstiges (30 Tage)", TimeUnit.DAYS.toMillis(30), 13),
    MISC_90_DAYS("Sonstiges (90 Tage)", TimeUnit.DAYS.toMillis(90), 14),
    MISC_PERM("Sonstiges (PERMANENT)", -1, 15),
    GRIEFING("Scamming/Griefing", TimeUnit.DAYS.toMillis(5), 16);

    @Getter
    private String reason;

    @Getter
    private long time;

    @Getter
    private int id;

    Reason(String reason, long time, int id) {
        this.reason = reason;
        this.time = time;
        this.id = id;
    }

    public static Reason getFromID(int id) {
        for (Reason reason : Reason.values()) {
            if (reason.getId() == id) {
                return reason;
            }
        }

        return null;
    }

}
