// Author: Ali Zarabi, Saadiq Shahsamand
// Filename: Storage.java
// Creation Date: Mar 25 2026
// Modified Date: Apr 2 2026
// Description: This is a storage script which saves the product backlog into the "backlog.dat" file.

package com.cps406.model;
import com.cps406.Main;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Storage {

    private static final String FILE_NAME = "backlog.dat";

    public static void save(ProductBacklog backlog) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            out.writeObject(backlog);

        } catch (IOException ioe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "Failed to save product backlog", ioe);
        }
    }

//    public static ProductBacklog load() {
//        File file = new File(FILE_NAME);
//
//        if (!file.exists()) {
//            return new ProductBacklog();
//        }
//
//        try (ObjectInputStream in =
//                     new ObjectInputStream(new FileInputStream(file))) {
//            return (ProductBacklog) in.readObject();
//
//        } catch (IOException | ClassNotFoundException e) {
//            Logger.getLogger(Main.class.getName())
//                    .log(Level.SEVERE, "Failed to load product backlog", e);
//            return new ProductBacklog();
//        }
//    }
}

