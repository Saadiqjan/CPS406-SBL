// Author: Saadiq Shahsamand, Harjap Uppal
// Filename: Sprint.java
// Date Created: Mar 19 2026
// Date Modified:
// Description: Encapsulate a single sprint

package com.cps406.model;

import com.cps406.DatabaseConnection;
import com.cps406.Main;

import java.io.Serial;
import java.io.Serializable;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sprint implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Store current sprint number, capacity, start and end dates, and status
    private final int sprintID;
    private final int capacity;
    private final LocalDate end;
    private final LocalDate start;

    // Store progress

    private final int totalDays;
    private float totalEffort;
    private float effortCompleted;

    /**
     * Create sprint
     * @param capacity of the sprint
     * @param end date of the sprint
     */
    public Sprint(int sprintID, int capacity, LocalDate end, int duration, float totalEffort, float effortCompleted) {
        // Set current sprint and increase total sprint count
        this.sprintID = sprintID;

        // Store parameters
        // Start is set to current date
        this.capacity = capacity;
        start = LocalDate.now();
        this.end = end;

        this.totalEffort = totalEffort;
        this.effortCompleted = effortCompleted;
        totalDays = duration * 7;
    }

    // Get items
    public ArrayList<Item> getItems() {
        ArrayList<Item> items = new ArrayList<>();

        String query = """
        SELECT pb.*, si.completed, si.completed_day
        FROM sprint_items si
        JOIN product_backlog pb
            ON si.item_name = pb.item_name
        JOIN sprints s
            ON si.sprint_id = s.sprint_id
        WHERE s.is_active = 1
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Item item =
                        new Item(
                                rs.getString("item_name"),
                                rs.getString("story"),
                                rs.getString("task"),
                                rs.getInt("priority"),
                                rs.getFloat("effort"),
                                rs.getFloat("time_estimate"),
                                rs.getFloat("risk"),
                                rs.getInt("complete") != 0,
                                rs.getInt("completion_day")
                        );

                items.add(item);
            }
        }
        catch (SQLException sqe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "SQL Execution Failed", sqe);
        }

        return items;
    }

    public int getID() {
        return sprintID;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public float getTotalEffort() {
        return totalEffort;
    }

    public void completeItem(boolean completion, Item item) {
        if (completion && !item.isComplete()) {
            updateEffortCompleted(item.getEffort());
        }
        else if (!completion && item.isComplete()) {
            updateEffortCompleted(item.getEffort() * -1);
        }

        item.setComplete(completion, getCurrentDay());
    }

    public void updateEffortCompleted(float effort) {
        String query = """
        UPDATE sprints
        SET effort_completed = effort_completed + ?
        WHERE sprint_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setFloat(1, effort);
            pstmt.setInt(2, sprintID);

            pstmt.executeUpdate();
        }
        catch (SQLException sqe) {

        }
    }

    // Calculate Progess
    public double getProgress() {
        return effortCompleted / totalEffort;
    }

    public float getRemEffort() {
        float remEffort = 0.0f;

        for (Item item: getItems()) {
            if (!item.isComplete()) {
                remEffort += item.getEffort();
            }
        }

        return remEffort;
    }

    public float getRemTime() {
        float remTime = 0.0f;

        for (Item item: getItems()) {
            if (!item.isComplete()) {
                remTime += item.getTime();
            }
        }

        return remTime;
    }

    public int getCurrentDay() {
        return (int) ChronoUnit.DAYS.between(start, LocalDate.now());
    }

    public int getCapacity() {
        return capacity;
    }

    public LocalDate getEnd() {
        return end;
    }

}
