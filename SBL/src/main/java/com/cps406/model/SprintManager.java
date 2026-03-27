// Author: Saadiq Shahsamand
// Filename: SprintManager.java
// Date Created: Mar 20 2026
// Date Modified: Mar 24
// Description: Manages the current sprint and stores previous sprints
//              also responsible for creating and finishing sprints

package com.cps406.model;

import java.time.LocalDate;
import java.util.ArrayList;

public class SprintManager {
    // Store previous Sprints
    // TODO: save previous sprints to a local file
    //       only retrieve for reading
    //       this arraylist should be then removed
    private ArrayList<Sprint> prevSprints;

    // Store current sprint
    private Sprint curSprint;

    /**
     * create sprint manager
     */
    public SprintManager() {
        prevSprints = new ArrayList<Sprint>();
        curSprint = null; // INIT THIS IN createSprint
    }

    // Getters
    public Sprint getCurSprint() {
        return curSprint;
    }

    /**
     * @return true if active sprint, false otherwise
     */
    public boolean isActiveSprint() {
        return curSprint != null;
    }


    /**
    *   Create Sprint
    *   @param capacity, endDate, backlog, selectedItems
    *   @return true if sprint was successfully created, false otherwise
     */
    public boolean createSprint(int capacity, LocalDate endDate, ProductBacklog backlog, ArrayList<Item> selectedItems) {

        // prevent multiple sprints
        if (curSprint != null) {
            return false;
        }

        // check if sprint is valid
        if (capacity <= 0) return false;
        if (endDate == null || endDate.isBefore(LocalDate.now())) return false;

        curSprint = new Sprint(capacity, endDate);

        // add items to sprint and remove from backlog
        for (Item item: selectedItems) {
            curSprint.getItems().add(item);
            backlog.removeItem(item.getName());
        }

        return true; // sprint was successfully created
    }

    /**
     * end sprint
     * @param backlog the product backlog
     */
    public void finishSprint(ProductBacklog backlog)
    {
        // Get the sprint items
        ArrayList<Item> sprintItems = curSprint.getItems();

        // Any incomplete sprint items should return back to the product backlog
        for (Item item : sprintItems) {
            if (item.getStatus() != Status.DONE) {
                backlog.addItem(item);
            }
        }

        //save old sprint and reset for new one
        prevSprints.add(curSprint);
        curSprint = null;
    }
}
