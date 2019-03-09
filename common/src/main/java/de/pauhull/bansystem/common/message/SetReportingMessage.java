package de.pauhull.bansystem.common.message;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by Paul
 * on 28.11.2018
 *
 * @author pauhull
 */
@ToString
public class SetReportingMessage extends PluginMessage {

    public static final String TYPE = "SET_REPORTING";

    @Getter
    private String reporting;

    @Getter
    private String reported;

    public SetReportingMessage(String reporting, String reported) {
        super(TYPE);
        this.reporting = reporting;
        this.reported = reported;
        this.set("reporting", reporting);
        this.set("reported", reported);
    }

    public SetReportingMessage(PluginMessage message) {
        this(message.getString("reporting"), message.getString("reported"));
    }

    public void sendToServer(String server) {
        TimoCloudAPI.getMessageAPI().sendMessageToServer(this, server);
    }

}
