// Author: Ali Zarabi
// Filename: Storage.java
// Creation Date: Mar 25 2026
// Modified Date: Mar 25 2026
// Description:

package com.cps406.model;
import java.io.*;

public class Storage {

    private static final String FILE_NAME = "backlog.dat";

    public static void save(ProductBacklog backlog) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            out.writeObject(backlog);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ProductBacklog load() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return new ProductBacklog();
        }

        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(file))) {
            return (ProductBacklog) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ProductBacklog();
        }
    }
}

