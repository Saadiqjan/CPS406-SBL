// Author: Saadiq Shahsamand, Ali Zarabi
// Filename: ProductBacklog.java
// Date Created: Mar 19 2026
// Date Modified:
// Description: the product backlog

package com.cps406.model;

import com.cps406.DatabaseConnection;
import com.cps406.Main;

import java.io.Serial;
import java.sql.*;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductBacklog implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Create new product backlog
     */
    public ProductBacklog() {

    }

    // Get product backlog
    public ArrayList<Item> getBacklog() {
        ArrayList<Item> items = new ArrayList<>();

        String query = "SELECT * FROM product_backlog";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Item item = new Item(
                        rs.getString("item_name"),
                        rs.getString("story"),
                        rs.getString("description"),
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

    /**
     * Add new item
     * @param newItem new item to be added
     * @return true if successful
     */
    public boolean addItem(Item newItem) {
        String query = "INSERT OR IGNORE INTO product_backlog " +
                "(item_name, story, task, priority, effort, time_estimate, risk) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newItem.getName());
            pstmt.setString(2, newItem.getStory());
            pstmt.setString(3, newItem.getTask());
            pstmt.setInt(4, newItem.getPriority());
            pstmt.setFloat(5, newItem.getEffort());
            pstmt.setFloat(6, newItem.getTime());
            pstmt.setFloat(7, newItem.getRisk());

            pstmt.executeQuery();
        }
        catch (SQLException sqe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "SQL Execution Failed", sqe);
        }

        return true;
    }

    // Remove item by name
    public boolean removeItem(String itemName) {
        String query = "DELETE FROM product_backlog WHERE item_name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, itemName);

            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;
        }
        catch (SQLException sqe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "SQL Execution Failed", sqe);
        }

        return false;
    }

    // Get item by name
    public Item getItem(String itemName) {
        String query = "SELECT * FROM product_backlog WHERE item_name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, itemName);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Item(
                        rs.getString("item_name"),
                        rs.getString("story"),
                        rs.getString("task"),
                        rs.getInt("priority"),
                        rs.getInt("effort"),
                        rs.getInt("time_estimate"),
                        rs.getInt("risk"),
                        rs.getInt("complete") != 0,
                        rs.getInt("completion_day")
                );
            }
        }
        catch (SQLException sqe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "SQL Execution Failed", sqe);
        }

        return null;
    }

    // Clear the Backlog ArrayList
    public void clearBacklog() {
        String query = "DELETE FROM product_backlog";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.executeUpdate();
        }
        catch (SQLException sqe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "SQL Execution Failed", sqe);
        }
    }
}
