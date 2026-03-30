// Author: Saadiq Shahsamand
// Filename: Sprint.java
// Date Created: Mar 19 2026
// Date Modified:
// Description: Encapsulate a single sprint

package com.cps406.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class Sprint implements Serializable {
    private static final long serialVersionUID = 1L;
    // Store the total number of sprints that have occured
    private static int totalSprints = 0;

    // Store current sprint number, capacity, start and end dates, and status
    private int curSprint;
    private int capacity;
    private LocalDate start;
    private LocalDate end;
    private Status status;

    // Store the items in the sprint
    private ArrayList<Item> items;

    /**
     * Create sprint
     * @param capacity of the sprint
     * @param end date of the sprint
     */
    public Sprint(int capacity, LocalDate end) {
        // Set current sprint and increase total sprint count
        totalSprints++;
        curSprint = totalSprints;

        // Store parameters
        // Start is set to current date
        this.capacity = capacity;
        start = LocalDate.now();
        this.end = end;
        this.status = Status.IN_PROGRESS;

        items = new ArrayList<>();
    }

    // Get items
    public ArrayList<Item> getItems() {
        return items;
    }

    // TODO: allow modification of sprint items
}
