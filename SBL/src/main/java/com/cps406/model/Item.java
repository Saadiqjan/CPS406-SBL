// Author: Saadiq Shahsamand, Ali Zarabi, Harjap Uppal
// Filename: Item.java
// Date Created: Mar 19 2026
// Date Modified: Mar 28 2026
// Description: Encapsulate a single item on a product backlog

package com.cps406.model;

import com.cps406.DatabaseConnection;

import java.io.Serial;
import java.sql.*;
import java.util.ArrayList;
import java.io.Serializable;

public class Item implements Comparable<Item>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Store item details
    private String name;
    private String story;
    private String task;
    private int priority;
    private float effort;
    private float time;
    private float risk;
    private Integer completionDay;

    // Store item status
    private boolean complete;

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
    public Item(String name, String story, String task, int priority, float effort, float time, float risk, boolean complete, Integer completionDay) {
        // Store parameters
        this.name = name;
        this.story = story;
        this.task = task;
        this.priority = priority;
        this.effort = effort;
        this.time = time;
        this.risk = risk;

        // Set up list of engineering tasks related to the item
        tasks = new ArrayList<>();

        // Set completion status
        this.complete = complete;
        this.completionDay = completionDay;
    }

    // Getters
    public String getName() { return name; }
    public String getStory() { return story; }
    public String getTask() { return task; }
    public int getPriority() { return priority; }
    public float getEffort() { return effort; }
    public float getTime() { return time; }
    public float getRisk() { return risk; }
    public Integer getCompletionDay() { return completionDay; }

    public ArrayList<Task> getTasks() { return tasks; }
    public boolean isComplete() {return complete;}

    // Setters
    public void setStory(String newStory) { story = newStory; saveItem(); }
    public void setTask(String newTask) { task = newTask; saveItem(); }
    public void setPriority(int newPriority) { priority = newPriority; saveItem(); }
    public void setEffort(float newEffort) { effort = newEffort; saveItem(); }
    public void setTime(float newTime) { time = newTime; saveItem(); }
    public void setRisk(float newRisk) { risk = newRisk; saveItem(); }
    public void setComplete(boolean complete, int day) {
        this.complete = complete;

        if (complete) {
            this.completionDay = day;
        }
        else {
            this.completionDay = null;
        }

        saveItem();
    }

    public void saveItem() {
        String query = """
        UPDATE product_backlog
        SET story = ?,
            task = ?,
            priority = ?,
            effort = ?,
            time_estimate = ?,
            risk = ?,
            complete = ?,
            completion_day = ?
        WHERE item_name = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, getStory());
            stmt.setString(2, getTask());
            stmt.setInt(3, getPriority());
            stmt.setFloat(4, getEffort());
            stmt.setFloat(5, getTime());
            stmt.setFloat(6, getRisk());
            stmt.setInt(7, complete ? 1 : 0);
            stmt.setInt(8, completionDay);

            stmt.setString(9, getName());

            stmt.executeUpdate();
        }
        catch (SQLException sqe) {

        }
    }

    public void setName(String newName) {

    }

    // Add a task
    public void addTask(Task task) {
        tasks.add(task);
    }

    public void setTaskComplete(boolean value, Task task) {
        task.setComplete(value);
    }

    /**
     * Get a task by its name
     * @param id id of the task
     * @return the Task if found, null otherwise
     */
    public Task getTask(int id) {
        for (Task task : tasks) {
            if (task.getID() == id) {
                return task;
            }
        }
        return null; // task not found
    }

    // Remove task by task name
    public void removeTask(int id) {
        tasks.removeIf(task -> task.getID() == id);
    }

    @Override
    public int compareTo(Item o) {
        return o.getPriority() - this.priority;
    }
}


