package de.pauhull.bansystem.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by Paul
 * on 30.11.2018
 *
 * @author pauhull
 */
@AllArgsConstructor
public class MuteInfo {

    @Getter
    private UUID uuid;

    @Getter
    private String muteId;

    @Getter
    private long time;

    @Getter
    private UUID mutedBy;

}