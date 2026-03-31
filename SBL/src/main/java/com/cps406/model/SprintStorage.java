// Author: Ali Zarabi, Harjap Uppal
// Filename: SprintStorage.java
// Creation Date: Mar 30 2026
// Description: This storage script saves sprint backlog into "sprint.dat" file.

package com.cps406.model;

import java.io.*;
import java.util.ArrayList;

public class SprintStorage {

    private static final String PREV_FILE = "prev_sprints.dat";
    private static final String CUR_FILE = "cur_sprint.dat";

    // Save the current sprint
    public static void saveCurSprint(Sprint curSprint) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(CUR_FILE))) {
            out.writeObject(curSprint);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load the current sprint
    public static Sprint loadCurSprint() {
        File file = new File(CUR_FILE);
        if (!file.exists()) return null;

        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(file))) {
            return (Sprint) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Append a finished sprint to the previous sprints file
    public static void savePreviousSprint(Sprint finishedSprint) {
        ArrayList<Sprint> prevSprints = loadPreviousSprints();
        prevSprints.add(finishedSprint);

        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(PREV_FILE))) {
            out.writeObject(prevSprints);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load all previous sprints (read-only)
    public static ArrayList<Sprint> loadPreviousSprints() {
        File file = new File(PREV_FILE);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(file))) {
            return (ArrayList<Sprint>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
