package com.cps406.model;

public class Task {
    private String name;

    public Task(String name) {
        this.name = name;
    }

    // Getters
    public String getName() { return name; }

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
