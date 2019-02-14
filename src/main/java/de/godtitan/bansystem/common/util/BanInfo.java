package de.godtitan.bansystem.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.net.InetAddress;
import java.util.UUID;

/**
 * Created by Paul
 * on 30.11.2018
 *
 * @author pauhull
 */
@AllArgsConstructor
public class BanInfo {

    @NonNull
    @Getter
    private UUID uuid;

    @NonNull
    @Getter
    private InetAddress address;

    @Getter
    @NonNull
    private long time;

    @Getter
    @NonNull
    private UUID bannedBy;

    @Getter
    @NonNull
    private Reason reason;

    public long getEnd() {
        if (reason.getTime() == -1) {
            return -1;
        } else {
            return time + reason.getTime();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BanInfo) {
            BanInfo info = (BanInfo) obj;

            return uuid.equals(info.uuid) && address.equals(info.address)
                    && time == info.time && bannedBy.equals(info.bannedBy)
                    && reason.equals(info.reason);
        }

        return super.equals(obj);
    }

}