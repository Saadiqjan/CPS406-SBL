// Author: Saadiq Shahsamand
// Filename: BaseController.java
// Date Created: Mar 24 2026
// Date Modified: Mar 25 2026
// Description: Controller super class that contains methods and attributes
//              all controls will require

package com.cps406.controllers;

import com.cps406.AppState;
import com.cps406.model.Item;
import javafx.animation.PauseTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BaseController {
    // Store stage scene and root
    // Required for switching between scenes
    protected Stage stage;
    protected Scene scene;
    protected Parent root;

    // Store the app state of the program
    // App state contains the product backlog and sprint manager
    protected AppState appState;

    // Set app state
    public void setAppState(AppState appState) {
        this.appState = appState;
    }

    /**
     * this method will initialize a backlog table and its columns
     * @param backlogTable the table
     * @param nameCol requirement name column
     * @param priorityCol priority column
     * @param effortCol effort column
     * @param timeCol time column
     * @param riskCol risk column
     */
    protected void setTableColumns(TableView<Item> backlogTable,
                                   TableColumn<Item, String> nameCol,
                                   TableColumn<Item, Integer> priorityCol,
                                   TableColumn<Item, Float> effortCol,
                                   TableColumn<Item, Float> timeCol,
                                   TableColumn<Item, Float> riskCol) {
        nameCol.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );

        priorityCol.setCellValueFactory(
                new PropertyValueFactory<>("priority")
        );

        effortCol.setCellValueFactory(
                new PropertyValueFactory<>("effort")
        );

        timeCol.setCellValueFactory(
                new PropertyValueFactory<>("time")
        );

        riskCol.setCellValueFactory(
                new PropertyValueFactory<>("risk")
        );

        // This will ensure that any negative risk values (risk not specified)
        // will appear as 'N/A' on the actual table
        riskCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Float value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText("");
                }
                else if (value == -1f) {
                    setText("N/A");
                }
                else {
                    setText(String.format("%.2f", value));
                }
            }
        });

        /**
         * Initialize a pop-up to appear when a task is hovered over for one second. This popup showcases
         * the task's story, task, priority, effort and risk in that order. Cascades text to ensure the popup
         * doesn't get too our of hand.
         **/
        backlogTable.setRowFactory(tv -> {
            TableRow<Item> row = new TableRow<>();
            Tooltip tooltip = new Tooltip();
            tooltip.setWrapText(true);
            tooltip.setMaxWidth(300);
            PauseTransition delay = new PauseTransition(Duration.seconds(0.75));

            row.setOnMouseEntered(event -> {
                if (row.isEmpty() || row.getItem() == null) {
                    return;
                }

                Item item = row.getItem();

                tooltip.setText(
                        "Story:\n" + item.getStory() + "\n\n" +
                                "Task:\n" + item.getTask() + "\n\n" +
                                "Priority: " + item.getPriority() + "\n" +
                                "Effort: " + item.getEffort() + "\n" +
                                "Risk: " + item.getRisk()
                );

                delay.setOnFinished(e -> {
                    if (row.isHover() && !row.isEmpty()) {
                        tooltip.show(
                                row,
                                event.getScreenX() + 10,
                                event.getScreenY() + 10
                        );
                    }
                });

                delay.playFromStart();
            });

            row.setOnMouseExited(event -> {
                delay.stop();
                tooltip.hide();
            });

            row.setOnMouseMoved(event -> {
                if (tooltip.isShowing()) {
                    tooltip.setX(event.getScreenX() + 10);
                    tooltip.setY(event.getScreenY() + 10);
                }
            });

            return row;
        });
    }
}
