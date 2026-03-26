// Author: Saadiq Shahsamand
// Filename: Task.java
// Date Created: Mar 19 2026
// Date Modified:
// Description: Encapsulate a single task

package com.cps406.model;

public class Task {
    // Store task details
    private String name;
    private String description;
    private Status status;

    /**
     * create a task
     * @param name of the task
     */
    public Task(String name) {
        // Store parameters
        this.name = name;
        status = Status.TODO;
    }

    // Getters
    public String getName() { return name; }
    public Status getStatus() { return status; }

    // Might remove this if unused
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof Task))
            return false;

        Task t = (Task)obj;

        if (name.equals(t.getName()))
            return true;

        return false;
    }
}
