// Author: Saadiq Shahsamand, Ali Zarabi
// Filename: ProductBacklog.java
// Date Created: Mar 19 2026
// Date Modified:
// Description: the product backlog

package com.cps406.model;

import com.cps406.DatabaseConnection;

import java.io.Serial;
import java.sql.*;
import java.util.ArrayList;
import java.io.Serializable;

public class ProductBacklog implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Store list of items
    private ArrayList<Item>  items;

    /**
     * Create new product backlog
     */
    public ProductBacklog() {
        items = new ArrayList<>();
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
                        rs.getFloat("risk")
                );

                items.add(item);
            }
        }
        catch (SQLException sqe) {

        }

        return items;
    }

    /**
     * Add new item
     * @param newItem new item to be added
     * @return true if successful
     */
    public boolean addItem(Item newItem) {
        // Check if any existing item shares the same name
        // If yes, do not add new item
        for (Item item : items) {
            if (item.getName().equals(newItem.getName())) {
                return false;
            }
        }

        // Add new item
        items.add(newItem);

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

        }

        return true;
    }

    // Remove item by name
    public void removeItem(String itemName) {
        items.removeIf(item -> item.getName().equals(itemName));
    }

    // Get item by name
    public Item getItem(String name) {
        // Loop through each item and check name equality
        for (Item item : items) {
            if (item.getName().equals(name)) {
                return item;
            }
        }

        return null;
    }

    // Clear the Backlog ArrayList
    public void clearBacklog() {items.clear();}
}
