// Author: Saadiq Shahsamand, Harjap Uppal
// Filename: Sprint.java
// Date Created: Mar 19 2026
// Date Modified:
// Description: Encapsulate a single sprint

package com.cps406.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Sprint implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    // Store the total number of sprints that have occured
    private static int totalSprints = 0;

    // Store current sprint number, capacity, start and end dates, and status
    private final int sprintID;
    private final int capacity;
    private final LocalDate end;
    private final LocalDate start;

    // Store the items in the sprint
    private ArrayList<Item> items;

    // Store progress

    private final int totalDays;
    private float totalEffort;
    private float effortCompleted;

    /**
     * Create sprint
     * @param capacity of the sprint
     * @param end date of the sprint
     */
    public Sprint(int sprintID, int capacity, LocalDate end, int duration) {
        // Set current sprint and increase total sprint count
        this.sprintID = sprintID;

        // Store parameters
        // Start is set to current date
        this.capacity = capacity;
        start = LocalDate.now();
        this.end = end;

        totalDays = duration * 7;
        items = new ArrayList<>();
    }

    // Get items
//    public ArrayList<Item> getItems() {
//        return items;
//    }
    public int getID() {
        return sprintID;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public float getTotalEffort() {
        return totalEffort;
    }

    public boolean addItem(Item item) {
        if (items.add(item)) {
            totalEffort += item.getEffort();
            return true;
        }

        return false;
    }

    public boolean removeItem(Item item) {
        if (items.remove(item)) {
            totalEffort -= item.getEffort();
            return true;
        }

        return false;
    }

    public void completeItem(boolean completion, Item item) {
        if (completion && !item.isComplete()) {
            effortCompleted += item.getEffort();
        }
        else if (!completion && item.isComplete()) {
            effortCompleted -= item.getEffort();
        }

        item.setComplete(completion, getCurrentDay());
    }

    // Calculate Progess
    public double getProgress() {
        return effortCompleted / totalEffort;
    }

    public float getRemEffort() {
        float remEffort = 0.0f;

        for (Item item: items) {
            if (!item.isComplete()) {
                remEffort += item.getEffort();
            }
        }

        return remEffort;
    }

    public float getRemTime() {
        float remTime = 0.0f;

        for (Item item: items) {
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
