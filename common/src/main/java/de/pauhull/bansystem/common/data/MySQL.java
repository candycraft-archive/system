package de.pauhull.bansystem.common.data;

import java.sql.*;

public class MySQL {

    private boolean ssl;
    private String host;
    private String port;
    private String database;
    private String user;
    private String password;

    private Connection connection;

    public MySQL(String host, String port, String database, String user, String password, boolean ssl) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
        this.ssl = ssl;
    }

    public boolean connect() {
        try {
            String connectionURL = String.format("jdbc:mysql://%s:%s/%s?autoReconnect=true&useSSL=%s", host, port, database, Boolean.toString(ssl));
            connection = DriverManager.getConnection(connectionURL, user, password);

            return true;
        } catch (SQLException e) {
            System.err.println("Konnte nicht zur MySQL verbinden: " + e.getMessage());
            return false;
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("MySQL konnte nicht beendet werden.");
        }
    }

    public void update(String sql) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet query(String query) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public PreparedStatement prepare(String sql) {
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
