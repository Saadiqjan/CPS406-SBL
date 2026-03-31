// Author: Saadiq Shahsamand, Harjap Uppal
// Filename: SprintManager.java
// Date Created: Mar 20 2026
// Date Modified: Mar 28
// Description: Manages the current sprint and stores previous sprints
//              also responsible for creating and finishing sprints

package com.cps406.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

public class SprintManager implements Serializable {
    private static final long serialVersionUID = 1L;

    // Store current sprint
    private Sprint curSprint;

    public SprintManager() {
        // Load current sprint if it exists
        curSprint = SprintStorage.loadCurSprint();
    }

    // Getters
    public Sprint getCurSprint() { return curSprint; }

    // Load previous sprints from file
    public ArrayList<Sprint> getPrevSprints() {
        return SprintStorage.loadPreviousSprints();
    }

    // Setters
    public void setCurSprint(Sprint sprint) { 
        curSprint = sprint; 
        SprintStorage.saveCurSprint(sprint);
    }

    // check if sprint is active
    public boolean isActiveSprint() {
        return curSprint != null;
    }

    // create a new sprint
    public boolean createSprint(int capacity, LocalDate endDate, int duration, ProductBacklog backlog, ArrayList<Item> selectedItems) {

        if (curSprint != null) return false; // prevent multiple sprints
        if (capacity <= 0) return false;
        if (endDate == null || endDate.isBefore(LocalDate.now())) return false;

        curSprint = new Sprint(capacity, endDate, duration);

        for (Item item : selectedItems) {
            curSprint.addItem(item);
            backlog.removeItem(item.getName());
        }

        SprintStorage.saveCurSprint(curSprint);
        return true;
    }

    // finish sprint
    public void finishSprint(ProductBacklog backlog) {
        if (curSprint == null) return;

        // Return incomplete items to backlog
        for (Item item : curSprint.getItems()) {
            if (!item.isComplete()) {
                backlog.addItem(item);
            }
        }

        // Append current sprint to previous sprints
        SprintStorage.savePreviousSprint(curSprint);

        // Clear current sprint
        curSprint = null;
        SprintStorage.saveCurSprint(null);
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
