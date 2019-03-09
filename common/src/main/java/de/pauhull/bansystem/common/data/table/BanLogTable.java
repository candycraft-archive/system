package de.pauhull.bansystem.common.data.table;

import de.pauhull.bansystem.common.data.MySQL;
import de.pauhull.bansystem.common.util.BanInfo;
import de.pauhull.bansystem.common.util.Reason;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * Created by Paul
 * on 27.11.2018
 *
 * @author pauhull
 */
public class BanLogTable {

    private static final String TABLE = "banlog";

    private MySQL mySQL;
    private ExecutorService executorService;

    public BanLogTable(MySQL mySQL, ExecutorService executorService) {
        this.mySQL = mySQL;
        this.executorService = executorService;

        mySQL.update("CREATE TABLE IF NOT EXISTS `" + TABLE + "` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(255), " +
                "`bannedip` VARCHAR(255), `time` BIGINT, `bannedby` VARCHAR(255), `reason` VARCHAR(255), PRIMARY KEY (`id`))");
    }

    public void log(UUID uuid, InetAddress ip, UUID bannedBy, Reason reason) {
        executorService.execute(() -> {

            mySQL.update("INSERT INTO `" + TABLE + "` VALUES (0, '" + uuid.toString() + "', '" + ip.getHostAddress() + "', "
                    + System.currentTimeMillis() + ", '" + bannedBy.toString() + "', " + reason.getId() + ")");

        });
    }

    public void getLogs(UUID uuid, Consumer<List<BanInfo>> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet result = mySQL.query("SELECT * FROM `" + TABLE + "` WHERE `uuid`='" + uuid.toString() + "'");

                List<BanInfo> bans = new ArrayList<>();
                while (result.next()) {
                    InetAddress address = InetAddress.getByName(result.getString("bannedip"));
                    long time = result.getLong("time");
                    UUID bannedBy = UUID.fromString(result.getString("bannedby"));
                    Reason reason = Reason.getFromID(result.getInt("reason"));
                    bans.add(new BanInfo(uuid, address, time, bannedBy, reason));
                }
                consumer.accept(bans);

            } catch (SQLException | UnknownHostException e) {
                e.printStackTrace();
                consumer.accept(new ArrayList<>());
            }
        });
    }

}
