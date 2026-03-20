package com.cps406;

import com.cps406.model.ProductBacklog;
import com.cps406.model.SprintManager;

public class AppState {
    private SprintManager sprintManager;
    private ProductBacklog productBacklog;

    public AppState() {
        sprintManager = new SprintManager();
        productBacklog = new ProductBacklog();
    }

    public SprintManager getSprintManager() {
        return sprintManager;
    }

    public ProductBacklog getProductBacklog() {
        return productBacklog;
    }
}
