package edu.yb.strtgst.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection dbConnection;
    private Connection connection;

    private final String url = "jdbc:mysql://localhost:3306/Strtgst";
    private final String user = "root";
    private final String password = "mysql";

    private DBConnection() throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
    }

    public static DBConnection getInstance() throws SQLException {
        return dbConnection == null ? new DBConnection() : dbConnection;
    }

    public Connection getConnection() {
        return connection;
    }
}
