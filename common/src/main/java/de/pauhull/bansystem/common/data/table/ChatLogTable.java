package de.pauhull.bansystem.common.data.table;

import de.pauhull.bansystem.common.data.MySQL;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class ChatLogTable {

    private static final String TABLE = "chatlog";

    private MySQL mySQL;
    private ExecutorService executorService;

    public ChatLogTable(MySQL mySQL, ExecutorService executorService) {
        this.mySQL = mySQL;
        this.executorService = executorService;

        mySQL.update("CREATE TABLE IF NOT EXISTS `" + TABLE + "` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(36), " +
                "`name` VARCHAR(16), `time` BIGINT, `message` VARCHAR(256), PRIMARY KEY (`id`))");
    }

    public void log(UUID uuid, String name, String message) {
        executorService.execute(() -> {

            mySQL.update("INSERT INTO `" + TABLE + "` VALUES (0, '" + uuid + "', '" + name + "', " + System.currentTimeMillis() + ", '" + message + "')");

        });
    }

}
