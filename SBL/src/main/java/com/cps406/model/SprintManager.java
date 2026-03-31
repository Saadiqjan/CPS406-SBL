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

    // Setters
    public void setCurSprint(Sprint sprint) {this.curSprint = sprint;}
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
    public boolean createSprint(int capacity, LocalDate endDate, int duration, ProductBacklog backlog, ArrayList<Item> selectedItems) {

        // prevent multiple sprints
        if (curSprint != null) {
            return false;
        }

        // check if sprint is valid
        if (capacity <= 0) return false;
        if (endDate == null || endDate.isBefore(LocalDate.now())) return false;

        curSprint = new Sprint(capacity, endDate, duration);

        // add items to sprint and remove from backlog
        for (Item item: selectedItems) {
            curSprint.addItem(item);
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
            if (!item.isComplete()) {
                backlog.addItem(item);
            }
        }

        //save old sprint and reset for new one
        prevSprints.add(curSprint);
        curSprint = null;
    }

    public ArrayList<Item> generateSprintBacklog (ArrayList<Item> productBacklog, int capacity) {
        // Store recommended list of sprint items
        ArrayList<Item> sprintList = new ArrayList<Item>();

        // Store total effort
        float totalTime = 0;
        float curTime = 0;

        // Sort list based on priority
        Collections.sort(productBacklog);

        // Add items to sprint list until capacity is exceeded
        for (int i = 0; i < productBacklog.toArray().length; i++) {
            curTime = productBacklog.get(i).getTime();

            if (totalTime + curTime < capacity) {
                totalTime += curTime;

                sprintList.add(productBacklog.get(i));
            }
        }

        return sprintList;
    }
}