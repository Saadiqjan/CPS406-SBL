// Author: Saadiq Shahsamand, Ali Zarabi
// Filename: Item.java
// Date Created: Mar 19 2026
// Date Modified:
// Description: Encapsulate a single item on a product backlog

package com.cps406.model;

import java.util.ArrayList;
import java.io.Serializable;

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    // Store item details
    private String name;
    private String story;
    private String task;
    private int priority;
    private float effort;
    private float time;
    private float risk;

    // Store item status
    private Status status;
    private boolean completed;

    // Store related engineering tasks
    private ArrayList<Task> tasks;

    /**
     * Create a new item
     *
     * @param name of the item
     * @param story the user story the form which the requirement comes from
     * @param task at hand to fulfill the requirement
     * @param priority of this item
     * @param effort estimated effort required
     * @param risk estimated risk
     */
    public Item(String name, String story, String task, int priority, float effort, float time, float risk) {
        // Store parameters
        this.name = name;
        this.story = story;
        this.task = task;
        this.priority = priority;
        this.effort = effort;
        this.time = time;
        this.risk = risk;

        // Set status
        status = Status.TODO;

        // Set up list of engineering tasks related to the item
        tasks = new ArrayList<Task>();

        // Set completion status
        // Note: likely to be removed, kind of redundant when status enum exists
        completed = false;
    }

    // Getters
    public String getName() { return name; }
    public String getStory() { return story; }
    public String getTask() { return task; }
    public int getPriority() { return priority; }
    public float getEffort() { return effort; }
    public float getTime() { return time; }
    public float getRisk() { return risk; }
    public Status getStatus() { return status; }
    public ArrayList<Task> getTasks() { return tasks; }

    // Setters
    public void setPriority(int newPriority) { priority = newPriority; }
    public void setEffort(int newEffort) { effort = newEffort; }
    public void settime(int newtime) { risk = newtime; }
    public void setRisk(int newRisk) { risk = newRisk; }
    public void setStatus(Status newStatus) { status = newStatus; };

    // Add a task
    public void addTask(Task task) {
        tasks.add(task);
    }

    // Get task by task name
    public void getTask(String taskname) {
        // TODO: implement this
    }

    // Remove task by task name
    public void removeTask(String taskName) {
        tasks.removeIf(task -> task.getName().equals(taskName));
    }
}
