// Author: Saadiq Shahsamand
// Filename: Task.java
// Date Created: Mar 19 2026
// Date Modified: Apr 2 2026
// Description: Encapsulate a single task

package com.cps406.model;

import java.io.Serial;
import java.io.Serializable;

public class Task implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static int nextId = 1;

    // Store task details
    private final int id;
    private final String description;
    private final int priority;
    private final float effort;
    private final float time;
    private boolean complete;

    /**
     *
     * @param description of the task
     * @param priority of the task
     * @param effort of the task
     * @param time of the task
     */
    public Task(String description, int priority, float effort, float time) {
        // Store parameters
        id = nextId++;
        this.description = description;
        this.priority = priority;
        this.effort = effort;
        this.time = time;

        complete = false;
    }

    // Getters
    public int getID() { return id; }
    public String getDescription() { return description; }
    public int getPriority() { return priority; }
    public float getEffort() { return effort; }
    public float getTime() { return time; }
    public boolean isComplete() { return complete; }

    public void setComplete(boolean value) { complete = value; }
}
