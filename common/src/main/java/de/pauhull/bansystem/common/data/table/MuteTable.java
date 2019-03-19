package de.pauhull.bansystem.common.data.table;

import de.pauhull.bansystem.common.data.MySQL;
import de.pauhull.bansystem.common.util.MuteInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class MuteTable {

    private static final String TABLE = "mutes";
    private static final Random RANDOM = new Random();

    private MySQL mySQL;
    private ExecutorService executorService;

    public MuteTable(MySQL mySQL, ExecutorService executorService) {
        this.mySQL = mySQL;
        this.executorService = executorService;

        mySQL.update("CREATE TABLE IF NOT EXISTS `" + TABLE + "` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(255), " +
                "`mute_id` VARCHAR(255), `time` BIGINT, `mutedby` VARCHAR(255), `muted_on` BIGINT, PRIMARY KEY (`id`))");
    }

    public void getAllMutes(Consumer<List<MuteInfo>> consumer) {
        executorService.execute(() -> {
            try {

                List<MuteInfo> muteInfos = new ArrayList<>();
                ResultSet result = mySQL.query("SELECT * FROM `" + TABLE + "`");

                while (result.next()) {
                    UUID uuid = UUID.fromString(result.getString("uuid"));
                    long mutedOn = result.getLong("muted_on");
                    long time = result.getLong("time");
                    String muteId = result.getString("mute_id");
                    UUID mutedBy = UUID.fromString(result.getString("mutedby"));
                    muteInfos.add(new MuteInfo(uuid, muteId, time, mutedBy, mutedOn));
                }

                consumer.accept(muteInfos);
            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(new ArrayList<>());
            }
        });
    }

    public void getMute(UUID uuid, Consumer<MuteInfo> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet result = mySQL.query("SELECT * FROM `" + TABLE + "` WHERE `uuid`='" + uuid + "'");

                if (result.next()) {
                    long mutedOn = result.getLong("muted_on");
                    long time = result.getLong("time");
                    String muteId = result.getString("mute_id");
                    UUID mutedBy = UUID.fromString(result.getString("mutedby"));
                    consumer.accept(new MuteInfo(uuid, muteId, time, mutedBy, mutedOn));
                    return;
                }

                consumer.accept(null);

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(null);
            }
        });
    }

    public void mute(UUID uuid, UUID mutedBy, long time, Consumer<String> consumer) {
        getMute(uuid, mute -> {
            generateFreeMuteId((muteId) -> {
                if (mute != null) {
                    String sql = "UPDATE `" + TABLE + "` SET `mute_id`='" + muteId
                            + "', `time`=" + time + ", `mutedby`='" + mutedBy + "', `muted_on`=" + System.currentTimeMillis() + " WHERE `uuid`='" + uuid.toString() + "'";
                    mySQL.update(sql);
                } else {
                    String sql = "INSERT INTO `" + TABLE + "` VALUES (0, '" + uuid + "', '" + muteId + "', "
                            + time + ", '" + mutedBy + "', " + System.currentTimeMillis() + ")";
                    mySQL.update(sql);
                }
                consumer.accept(muteId);
            });
        });
    }

    public void unmute(UUID uuid) {
        executorService.execute(() -> {
            mySQL.update("DELETE FROM `" + TABLE + "` WHERE `uuid`='" + uuid.toString() + "'");
        });
    }

    public void generateFreeMuteId(Consumer<String> consumer) {
        executorService.execute(() -> {

            char[] availableChars = "abcdefghijklmnopqrstuvqxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
            String muteId = null;
            do {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < 10; i++) {
                    builder.append(availableChars[RANDOM.nextInt(availableChars.length)]);
                }

                try {
                    ResultSet result = mySQL.query("SELECT * FROM `" + TABLE + "` WHERE `mute_id`='" + builder.toString() + "'");
                    if (!result.isBeforeFirst()) {
                        muteId = builder.toString();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            } while (muteId == null);

            consumer.accept(muteId);
        });
    }

}
