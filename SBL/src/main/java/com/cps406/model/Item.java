// Author: Saadiq Shahsamand, Ali Zarabi, Harjap Uppal
// Filename: Item.java
// Date Created: Mar 19 2026
// Date Modified: Mar 28 2026
// Description: Encapsulate a single item on a product backlog

package com.cps406.model;

import com.cps406.DatabaseConnection;
import com.cps406.Main;

import java.io.Serial;
import java.sql.*;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();

        String query = """
        SELECT * FROM tasks
        WHERE item_name = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, getName());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tasks.add(
                        new Task(
                                rs.getInt("task_id"),
                                rs.getString("description"),
                                rs.getInt("priority"),
                                rs.getFloat("effort"),
                                rs.getFloat("time_estimate"),
                                rs.getInt("complete") != 0
                        )
                );
            }
        }
        catch (SQLException sqe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "SQL Execution Failed", sqe);
        }

        return tasks;
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
            if (completionDay == null) {
                stmt.setNull(1, Types.INTEGER);
            } else {
                stmt.setInt(1, priority);
            }

            stmt.setString(9, getName());

            stmt.executeUpdate();
        }
        catch (SQLException sqe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "SQL Execution Failed", sqe);
        }
    }

    public void setName(String newName) {

    }

    // Add a task
    public void addTask(Task task) {

        String query = """
        INSERT INTO tasks
        (item_name, description, priority,
         effort, time_estimate, complete)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, getName());
            stmt.setString(2, task.getDescription());
            stmt.setInt(3, task.getPriority());
            stmt.setFloat(4, task.getEffort());
            stmt.setFloat(5, task.getTime());
            stmt.setInt(6, task.isComplete() ? 1 : 0);

            stmt.execute();
        }
        catch (SQLException sqe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "SQL Execution Failed", sqe);
        }
    }

    public void setTaskComplete(boolean value, Task task) {
        task.setComplete(value, getName());
    }

    /**
     * Get a task by its name
     * @param id id of the task
     * @return the Task if found, null otherwise
     */
    public Task getTask(int id) {
        for (Task task : getTasks()) {
            if (task.getID() == id) {
                return task;
            }
        }
        return null; // task not found
    }

    @Override
    public int compareTo(Item o) {
        return o.getPriority() - this.priority;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        Item i = (Item) obj;

        if (i.getName().equals(getName())) {
            return true;
        }

        return false;
    }
}


