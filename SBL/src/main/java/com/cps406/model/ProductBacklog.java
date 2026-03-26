package com.cps406.model;

import java.util.ArrayList;
import java.io.Serializable;

public class ProductBacklog implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<Item> items;

    public ProductBacklog() {
        items = new ArrayList<Item>();
    }

    public ArrayList<Item> getBacklog() {
        return items;
    }

    public boolean addItem(Item newItem) {
        for (Item item : items) {
            if (item.getName().equals(newItem.getName())) {
                return false;
            }
        }

        items.add(newItem);
        return true;
    }

    public void removeItem(String itemName) {
        items.removeIf(item -> item.getName().equals(itemName));
    }

    public Item getItem(String name) {
        for (Item item : items) {
            if (item.getName().equals(name)) {
                return item;
            }
        }

        return null;
    }

    //Clear the Backlog ArrayList
    public void clearBacklog() {items.clear();}
}
