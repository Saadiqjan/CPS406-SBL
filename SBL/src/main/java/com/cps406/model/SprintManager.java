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
        curSprint = new Sprint(20, LocalDate.now().plusWeeks(2));
    }

    // Getters
    public Sprint getCurSprint() {
        return curSprint;
    }

    /**
     * @return true if active sprint, false otherwise
     */
    public boolean isActiveSprint() {
        if (curSprint == null)
            return false;

        return true;
    }

    // TODO: implement this
    //       note: items added to a sprint should be removed from the product backlog
    // public boolean createSprint()

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
    }
}
