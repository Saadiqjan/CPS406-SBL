// Author: Saadiq Shahsamand, Harjap Uppal
// Filename: SprintManager.java
// Date Created: Mar 20 2026
// Date Modified: Mar 28
// Description: Manages the current sprint and stores previous sprints
//              also responsible for creating and finishing sprints

package com.cps406.model;

import com.cps406.DatabaseConnection;
import com.cps406.Main;

import java.io.Serial;
import java.io.Serializable;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SprintManager implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Store current sprint
    private Sprint curSprint;

    public SprintManager() {
        // Load current sprint if it exists
        //curSprint = SprintStorage.loadCurSprint();
    }

    // Getters
    public Sprint getCurSprint() {
        String query = "SELECT * FROM sprints WHERE is_active = 1";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return new Sprint(
                    rs.getInt("sprint_id"),
                    rs.getInt("capacity"),
                    LocalDate.parse(rs.getString("end_date")),
                    rs.getInt("duration")
                );
            }
        }
        catch (SQLException sqe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "SQL Execution Failed", sqe);
        }

        return null;
    }

    // Load previous sprints from file
    public ArrayList<Sprint> getPrevSprints() {
        ArrayList<Sprint> prevSprints = new ArrayList<>();
        String query = "SELECT * FROM sprints WHERE is_active = 0";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                prevSprints.add(new Sprint(
                        rs.getInt("sprint_id"),
                        rs.getInt("capacity"),
                        LocalDate.parse(rs.getString("end_date")),
                        rs.getInt("duration")
                ));
            }
        }
        catch (SQLException sqe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "SQL Execution Failed", sqe);
        }

        return prevSprints;
    }

    // check if sprint is active
    public boolean isActiveSprint() {
        String query = "SELECT EXISTS(SELECT 1 FROM sprints WHERE is_active = 1)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            return rs.next() && rs.getBoolean(1);
        } catch (SQLException sqe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "SQL Execution Failed", sqe);
        }

        return false;
    }

    // create a new sprint
    public boolean createSprint(int capacity, LocalDate endDate, int duration, ProductBacklog backlog, ArrayList<Item> selectedItems) {

        if (isActiveSprint()) return false; // prevent multiple sprints
        if (capacity <= 0) return false;
        if (endDate == null || endDate.isBefore(LocalDate.now())) return false;

        String query = "INSERT INTO sprints" +
                "(capacity, end_date, duration, is_active) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, capacity);
            pstmt.setString(2, endDate.toString());
            pstmt.setInt(3, duration);
            pstmt.setInt(4, 1);
        }
        catch (SQLException sqe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "SQL Execution Failed", sqe);

            return false;
        }

        for (Item item : selectedItems) {
            addSprintItem(item, getCurSprint().getID());
            backlog.removeItem(item.getName());
        }

        return true;
    }

    public boolean addSprintItem(Item item, int sprintID) {
        String query = """
        INSERT OR IGNORE INTO sprint_items
        (sprint_id, item_name, completed, completed_day)
        VALUES (?, ?, 0, NULL)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, sprintID);
            stmt.setString(2, item.getName());

            return stmt.executeUpdate() > 0;
        }
        catch (SQLException sqe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "SQL Execution Failed", sqe);
        }

        return false;
    }

    // finish sprint
    public void finishSprint() {
        if (!isActiveSprint()) return;

        String query = """
        UPDATE sprints
        SET is_active = 0,
        WHERE sprint_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(2, getCurSprint().getID());

            stmt.executeUpdate();
        }
        catch (SQLException sqe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "SQL Execution Failed", sqe);
        }
    }

    /** Generate recommended sprint backlog */
    public ArrayList<Item> generateSprintBacklog(ArrayList<Item> productBacklog, int capacity) {
        ArrayList<Item> sprintList = new ArrayList<>();
        float totalTime = 0;

        Collections.sort(productBacklog);

        for (Item item : productBacklog) {
            float curTime = item.getTime();
            if (totalTime + curTime < capacity) {
                totalTime += curTime;
                sprintList.add(item);
            }
        }

        return sprintList;
    }
}
