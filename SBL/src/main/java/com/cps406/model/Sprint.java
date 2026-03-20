package com.cps406.model;

import java.time.LocalDate;

public class Sprint {
    private static int totalSprints = 0;
    private int curSprint;
    private int capacity;
    private LocalDate start;
    private LocalDate end;
    private int status;

    public Sprint(int capacity, LocalDate end, int status) {
        totalSprints++;
        curSprint = totalSprints;

        this.capacity = capacity;
        start = LocalDate.now();
        this.end = end;
        this.status = status;
    }
}
