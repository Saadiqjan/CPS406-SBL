// Author: Saadiq Shahsamand
// Filename: Task.java
// Date Created: Mar 19 2026
// Date Modified:
// Description: Encapsulate a single task

package com.cps406.model;

import java.io.Serializable;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int nextId = 1;

    // Store task details
    private int id;
    private String description;
    private int priority;
    private float effort;
    private float time;
    private boolean complete;

    /**
     *
     * @param description
     * @param priority
     * @param effort
     * @param time
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
