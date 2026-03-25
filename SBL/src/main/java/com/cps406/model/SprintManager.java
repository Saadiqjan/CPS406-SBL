package com.cps406.model;

import java.util.ArrayList;

public class SprintManager {
    private ArrayList<Sprint> prevSprints;
    private Sprint curSprint;

    public SprintManager() {
        prevSprints = new ArrayList<Sprint>();
        curSprint = null;
    }

    public Sprint getCurSprint() {
        return curSprint;
    }

    public boolean isActiveSprint() {
        if (curSprint == null)
            return false;

        return true;
    }

    // public boolean createSprint()

    public void finishSprint(ProductBacklog backlog)
    {
        ArrayList<Item> sprintItems = curSprint.getItems();

        for (Item item : sprintItems) {
            if (item.getStatus() != Status.DONE) {
                backlog.addItem(item);
            }
        }
    }
}
