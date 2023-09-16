package org.matveyvs.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static final String URL_KEY = "postgres.db";
    private static final String URL_NAME = "postgres.name";
    private static final String URL_PASSWORD = "postgres.password";
    private static final String URL_DRIVER = "postgres.driver";

    static {
        loadDriver();
    }

    private static void loadDriver() {
        try {
            Class.forName(PropertiesUtil.get(URL_DRIVER));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection open() {
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.get(URL_KEY),
                    PropertiesUtil.get(URL_NAME),
                    PropertiesUtil.get(URL_PASSWORD));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
