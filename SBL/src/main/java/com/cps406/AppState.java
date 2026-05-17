// Author: Saadiq Shahsamand, Ali Zarabi, Harjap Uppal
// Filename: AppState.java
// Date Created:
// Date Modified:
// Description: This is a wrapper class containing the sprint manager and product backlog.
//              This will be passed from controller to controller as scenes switch using
//              the method setAppState (part of base controller super class).
//              This will allow for controllers to access the product backlog, current sprint
//              and so on...

package com.cps406;

import com.cps406.model.ProductBacklog;
import com.cps406.model.SprintManager;
import com.cps406.model.Storage;

public class AppState {
    // Store the product backlog and sprint manager
    private SprintManager sprintManager;
    private ProductBacklog productBacklog;

    /**
     * Create app state
     */
    public AppState() {
        // initialize sprint manager which loads the current sprint automatically
        sprintManager = new SprintManager();

        // load product backlog
        productBacklog = Storage.load();
    }

    // Save the backlog
    public void saveBacklog() { Storage.save(productBacklog); }

    // save current sprint
    public void saveCurSprint() {
        if (sprintManager.getCurSprint() != null) {
            //sprintManager.setCurSprint(sprintManager.getCurSprint());
        }
    }

    // Getters
    public SprintManager getSprintManager() { return sprintManager; }
    public ProductBacklog getProductBacklog() { return productBacklog; }
}
