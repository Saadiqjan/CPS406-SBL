// Author: Ali Zarabi
// Filename: SprintStorage.java
// Creation Date: Mar 30 2026
// Description: This storage script saves sprint backlog into "sprint.dat" file.

package com.cps406.model;
import java.io.*;

public class SprintStorage {

    private static final String FILE_NAME = "sprint.dat";

    public static void save(SprintManager sprintManager) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            out.writeObject(sprintManager);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SprintManager load() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return new SprintManager();
        }

        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(file))) {

            return (SprintManager) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new SprintManager();
        }
    }
}