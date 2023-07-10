package vn.aptech.componentmanagementapp.util;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;

public class DatabaseConnection {

    private static final String HOST = "localhost";
    private static final String PORT = "5432";
    private static final String NAME = "QLLK";
    private static final String URL = "jdbc:postgresql://" + HOST +":" + PORT + "/" + NAME;
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "trancckute11";
    private static Connection connection;

    private DatabaseConnection() {

        Dotenv dotenv = Dotenv.load();
        try {
            connection = DriverManager.getConnection(URL, dotenv.get("DB_USERNAME"), dotenv.get("DB_PASSWORD"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        if (connection == null) {
            synchronized (DatabaseConnection.class) {
                if (connection == null) {
                    new DatabaseConnection();
                }
            }
        }
        return connection;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
