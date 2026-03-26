// Author: Saadiq Shahsamand, Ali Zarabi
// Filename: ProductBacklog.java
// Date Created: Mar 19 2026
// Date Modified:
// Description: the product backlog

package com.cps406.model;

import java.util.ArrayList;
import java.io.Serializable;

public class ProductBacklog implements Serializable {
    private static final long serialVersionUID = 1L;

    // Store list of items
    private ArrayList<Item> items;

    /**
     * Create new product backlog
     */
    public ProductBacklog() {
        items = new ArrayList<Item>();
    }

    // Get product backlog
    public ArrayList<Item> getBacklog() {
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
