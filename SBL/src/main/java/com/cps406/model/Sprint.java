package com.cps406.model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Sprint {
    private static int totalSprints = 0;
    private int curSprint;
    private int capacity;
    private LocalDate start;
    private LocalDate end;
    private Status status;

    private ArrayList<Item> items;

    public Sprint(int capacity, LocalDate end) {
        totalSprints++;
        curSprint = totalSprints;

        this.capacity = capacity;
        start = LocalDate.now();
        this.end = end;
        this.status = Status.IN_PROGRESS;
    }

    public ArrayList<Item> getItems() {
        return items;
    }
}
