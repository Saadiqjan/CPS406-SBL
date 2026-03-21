package com.cps406.model;

import java.util.ArrayList;

public class Item {
    private String name;
    private String description;
    private int priority;
    private float effort;
    private float risk;
    private Status status;
    private boolean completed;

    private ArrayList<Task> tasks;

    public Item(String name, String description, int priority, float effort, float risk) {
        this.description = description;
        this.name = name;
        this.priority = priority;
        this.effort = effort;
        this.risk = risk;
        status = Status.TODO;

        // Set up list of engineering tasks related to the item
        tasks = new ArrayList<Task>();

        completed = false;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPriotiy() { return priority; }
    public float getEffort() { return effort; }
    public float getRisk() { return risk; }
    public Status getStatus() { return status; }
    public ArrayList<Task> getTasks() { return tasks; }

    // Setters
    public void setPriority(int newPriority) { priority = newPriority; }
    public void setEffort(int newEffort) { effort = newEffort; }
    public void setRisk(int newRisk) { risk = newRisk; }
    public void setStatus(Status newStatus) { status = newStatus; };

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void getTask(String taskname) {

    }

    public void removeTask(String taskName) {
        tasks.removeIf(task -> task.getName().equals(taskName));
    }
}
