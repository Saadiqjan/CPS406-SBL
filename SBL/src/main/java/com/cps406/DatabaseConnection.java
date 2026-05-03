package com.cps406;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:app.db";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initialize() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(
                """
                    CREATE TABLE IF NOT EXISTS product_backlog (\
                    item_name TEXT UNIQUE NOT NULL\
                    story TEXT\
                    task TEXT\
                    priority INT\
                    effort FLOAT\
                    time_estimate FLOAT\
                    risk FLOAT\
                    )"""
            );
        }
        catch (SQLException se) {

        }
    }
}
