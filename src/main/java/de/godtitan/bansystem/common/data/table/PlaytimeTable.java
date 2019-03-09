package de.godtitan.bansystem.common.data.table;

import de.godtitan.bansystem.common.data.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class PlaytimeTable {

    private final String TABLE = "playtime";

    private MySQL mySQL;
    private ExecutorService executorService;
    private HashMap<UUID, Long> cache;

    public PlaytimeTable(MySQL mySQL, ExecutorService executorService) {
        this.mySQL = mySQL;
        this.executorService = executorService;
        this.cache = new HashMap<>();
        mySQL.update("CREATE TABLE IF NOT EXISTS `" + TABLE + "` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(255), `time` BIGINT, PRIMARY KEY (`id`))");
    }

    public void logJoin(UUID uuid) {
        if (cache.containsKey(uuid)) {
            logDisconnect(uuid);
        }
        cache.put(uuid, System.currentTimeMillis());
    }
    public void logDisconnect(UUID uuid) {
        executorService.execute(() -> {
            if (cache.containsKey(uuid)) {
                long time = cache.get(uuid);
                cache.remove(uuid);
                long now = System.currentTimeMillis();
                long result = now - time;
                ResultSet resultSet = mySQL.query("SELECT * FROM `" + TABLE + "` WHERE `uuid`='"+ uuid +"'");
                try {
                    if (resultSet.next()) {
                        result = result + resultSet.getLong("time");
                        mySQL.update("UPDATE `" + TABLE + "` SET `time`='"+ result +"' WHERE `uuid`='"+ uuid +"'");
                    }else {
                        mySQL.update("INSERT INTO `" + TABLE + "`(`id`, `uuid`, `time`) VALUES ('0','"+ uuid +"','"+ result +"')");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    public long getPlaytime(UUID uuid) {
        long result = -1;
        ResultSet resultSet = mySQL.query("SELECT * FROM `" + TABLE + "` WHERE `uuid`='"+ uuid +"'");
        try {
            if (cache.containsKey(uuid)) {
                result = System.currentTimeMillis() - cache.get(uuid);
            }
            if (resultSet.next()) {
                result = result + resultSet.getLong("time");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


}
