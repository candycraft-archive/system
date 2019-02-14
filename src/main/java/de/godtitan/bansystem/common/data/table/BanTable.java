package de.godtitan.bansystem.common.data.table;

import de.godtitan.bansystem.common.data.MySQL;
import de.godtitan.bansystem.common.util.BanInfo;
import de.godtitan.bansystem.common.util.Reason;

import javax.annotation.Nullable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class BanTable {

    private static final String TABLE = "bans";

    private MySQL mySQL;
    private ExecutorService executorService;
    private BanLogTable banLogTable;

    public BanTable(MySQL mySQL, ExecutorService executorService, BanLogTable banLogTable) {
        this.mySQL = mySQL;
        this.executorService = executorService;
        this.banLogTable = banLogTable;

        mySQL.update("CREATE TABLE IF NOT EXISTS `" + TABLE + "` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(255), " +
                "`bannedip` VARCHAR(255), `time` BIGINT, `bannedby` VARCHAR(255), `reason` VARCHAR(255), PRIMARY KEY (`id`))");
    }

    public void getBan(UUID uuid, Consumer<BanInfo> consumer) {
        getBan(uuid, null, consumer);
    }

    public void getBan(UUID uuid, @Nullable InetAddress ip, Consumer<BanInfo> consumer) {
        executorService.execute(() -> {
            try {

                String sql = "SELECT * FROM `" + TABLE + "` WHERE `uuid`='" + uuid.toString() + "'";
                if (ip != null) {
                    sql += " OR `bannedip`='" + ip.getHostAddress() + "'";
                }

                ResultSet result = mySQL.query(sql);
                if (result.next()) {
                    InetAddress address = InetAddress.getByName(result.getString("bannedip"));
                    long time = result.getLong("time");
                    UUID bannedBy = UUID.fromString(result.getString("bannedby"));
                    Reason reason = Reason.getFromID(result.getInt("reason"));
                    consumer.accept(new BanInfo(uuid, address, time, bannedBy, reason));
                    return;
                }

                consumer.accept(null);

            } catch (SQLException | UnknownHostException e) {
                e.printStackTrace();
                consumer.accept(null);
            }
        });
    }

    public void ban(UUID uuid, InetAddress ip, UUID bannedBy, Reason reason) {
        getBan(uuid, ban -> {

            banLogTable.log(uuid, ip, bannedBy, reason);

            if (ban != null) {
                mySQL.update("UPDATE `" + TABLE + "` SET `bannedip`='" + ip.getHostAddress()
                        + "', `time`=" + System.currentTimeMillis() + ", `bannedby`='" + bannedBy.toString() + "', `reason`=" + reason.getId() + " WHERE `uuid`='" + uuid.toString() + "'");
            } else {
                mySQL.update("INSERT INTO `" + TABLE + "` VALUES (0, '" + uuid.toString() + "', '" + ip.getHostAddress() + "', "
                        + System.currentTimeMillis() + ", '" + bannedBy.toString() + "', " + reason.getId() + ")");
            }

        });
    }

    public void unban(UUID uuid) {
        getBan(uuid, ban -> {

            if (ban != null) {
                mySQL.update("DELETE FROM `" + TABLE + "` WHERE `uuid`='" + uuid.toString() + "'");
            }

        });
    }

}
