package com.cps406;

import com.cps406.model.ProductBacklog;
import com.cps406.model.SprintManager;
import com.cps406.model.Storage;

public class AppState {
    private SprintManager sprintManager;
    private ProductBacklog productBacklog;

    public AppState() {
        sprintManager = new SprintManager();
        productBacklog = Storage.load();
    }

    public void saveBacklog() { Storage.save(productBacklog); }

    public SprintManager getSprintManager() {
        return sprintManager;
    }

    public ProductBacklog getProductBacklog() {
        return productBacklog;
    }
}
