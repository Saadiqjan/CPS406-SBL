package com.cps406.model;

import java.util.ArrayList;

public class Item {
    private String name;
    private String description;
    private int priority;
    private float effort;
    private float risk;
    private int status;

    private ArrayList<Task> tasks;

    public Item(String name, String description, int priority, float effort, float risk, int status) {
        this.description = description;
        this.name = name;
        this.priority = priority;
        this.effort = effort;
        this.risk = risk;
        this.status = status;

        // Set up list of engineering tasks related to the item
        tasks = new ArrayList<Task>();
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPriotiy() { return priority; }
    public float getEffort() { return effort; }
    public float getRisk() { return risk; }
    public int getStatus() { return status; }
    public ArrayList<Task> getTasks() { return tasks; }

    // Setters
    public void setPriority(int newPriority) { priority = newPriority; }
    public void setEffort(int newEffort) { effort = newEffort; }
    public void setRisk(int newRisk) { risk = newRisk; }
    public void setStatus(int newStatus) { status = newStatus; };

    public void addTask(Task task) {
        tasks.add(task);
    }

    public Task removeTask(String taskName) {
        Task task = null;

        return task;
    }
}
