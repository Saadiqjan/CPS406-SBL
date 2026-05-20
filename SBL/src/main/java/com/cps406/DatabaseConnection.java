package com.cps406;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                        item_name TEXT UNIQUE NOT NULL,\
                        story TEXT,\
                        task TEXT,\
                        priority INT,\
                        effort FLOAT,\
                        time_estimate FLOAT,\
                        risk FLOAT,\
                        complete INT DEFAULT 0\
                    )"""
            );

            stmt.execute(
                 """
                 CREATE TABLE IF NOT EXISTS sprints (\
                     sprint_id INTEGER PRIMARY KEY AUTOINCREMENT,\
                     capacity INT,\
                     end_date TEXT NOT NULL,\
                     duration INT,\
                     is_active INT DEFAULT 0,\
                     total_effort FLOAT DEFAULT 0,\
                     effort_completed FLOAT DEFAULT 0\
                 )"""
            );

            stmt.execute(
                """
                CREATE TABLE IF NOT EXISTS sprint_items (\
                    sprint_id INT,\
                    item_name TEXT,\
                    completed INT DEFAULT 0,\
                    completed_day TEXT,\
                    PRIMARY KEY (sprint_id, item_name),\
                    FOREIGN KEY (sprint_id) REFERENCES sprints(sprint_id),\
                    FOREIGN KEY (item_name) REFERENCES product_backlog(item_name)\
                )"""
            );
        }
        catch (SQLException sqe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "SQL Execution Failed", sqe);
        }
    }
}
