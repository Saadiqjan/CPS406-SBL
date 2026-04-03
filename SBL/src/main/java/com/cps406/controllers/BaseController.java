// Author: Saadiq Shahsamand, Ali Zarabi
// Filename: BaseController.java
// Date Created: Mar 24 2026
// Date Modified: Apr 2 2026
// Description: Base controller class providing shared functionality for all UI controllers.
//              Includes scene management, access to AppState, and reusable table configuration logic.

package com.cps406.controllers;

import com.cps406.AppState;
import com.cps406.Main;
import com.cps406.model.Item;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseController {
    // Store file paths
    protected final String styleSheetPath = "/com/cps406/styles.css";
    protected final String dashboardPath = "/com/cps406/Dashboard.fxml";
    protected final String sprintPath = "/com/cps406/Sprint.fxml";
    protected final String createSprintPath = "/com/cps406/CreateSprint.fxml";
    protected final String backlogPath = "/com/cps406/Backlog.fxml";

    // Store stage scene and root
    // Required for switching between scenes
    protected Stage stage;
    protected Scene scene;
    protected Parent root;

    // Store the app state of the program
    // App state contains the product backlog and sprint manager
    protected AppState appState;

    /**
     * Set appstate
     */
    public void setAppState(AppState appState) {
        this.appState = appState;
    }

    /**
     * load dashboard scene
     * @param event for stage
     */
    protected void loadDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(dashboardPath));
            root = loader.load();

            // Set the app state of the controller for the dashboard scene
            DashboardController dbc = loader.getController();
            dbc.setAppState(appState);

            // Create and set the scene
            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(styleSheetPath)).toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException ioe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "Failed to load scene: " + dashboardPath, ioe);
        } catch (IllegalStateException ise) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "FXML resource not found: " + dashboardPath, ise);
        } catch (NullPointerException npe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "Failed to load style sheet: " + styleSheetPath, npe);
        }
    }

    /**
     * load sprint scene
     * @param event for stage
     */
    protected void loadSprint(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(sprintPath));
            root = loader.load();

            // Set the app state of the controller for the sprint scene
            SprintController sc = loader.getController();
            sc.setAppState(appState);

            // Create and set the scene
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(styleSheetPath)).toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException ioe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "Failed to load scene: " + sprintPath, ioe);
        } catch (IllegalStateException ise) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "FXML resource not found: " + sprintPath, ise);
        } catch (NullPointerException npe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "Failed to load style sheet: " + styleSheetPath, npe);
        }
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
        // Set the property values for each column
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

        // Initialize a pop-up to appear when a task is hovered over for one second. This popup showcases
        // the task's story, task, priority, effort and risk in that order. Cascades text to ensure the popup
        // doesn't get too our of hand.
        backlogTable.setRowFactory(tv -> {
            TableRow<Item> row = new TableRow<>() {
                @Override
                protected void updateItem(Item item, boolean empty) {
                    super.updateItem(item, empty);

                    // Reset style for empty rows
                    if (empty || item == null) {
                        setStyle("");
                    // Visually indicate completed items
                    } else if (item.isComplete()) {
                        setStyle("-fx-opacity: 0.5;");
                    } else {
                        setStyle("");
                    }
                }
            };
            // Tooltip for displaying detailed item information
            Tooltip tooltip = new Tooltip();
            tooltip.setWrapText(true);
            tooltip.setMaxWidth(300);
            PauseTransition delay = new PauseTransition(Duration.seconds(0.75));

            // Show tooltip after delay when hovering over a row
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
                                "Time Estimate: " + item.getTime() + "\n" +
                                "Risk: " + item.getRisk()
                );

                // Only show tooltip if cursor remains on the row
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

            // Cancel tooltip if cursor leaves the row
            row.setOnMouseExited(event -> {
                delay.stop();
                tooltip.hide();
            });

            // Keep tooltip positioned near cursor while moving
            row.setOnMouseMoved(event -> {
                if (tooltip.isShowing()) {
                    tooltip.setX(event.getScreenX() + 10);
                    tooltip.setY(event.getScreenY() + 10);
                }
            });

            return row;
        });
    }

    /**
     * add a unary operator filter to ensure proper input for
     * the priority field, effort field, and time field
     * @param priorityField priority field
     * @param effortField effort field
     * @param timeField time field
     */
    protected void addUnaryOperator(TextField priorityField, TextField effortField, TextField timeField) {

        // Filter non integer values outside the range 1-3
        UnaryOperator<TextFormatter.Change> priorityFilter = change -> {
            String text = change.getControlNewText();

            if (text.isEmpty()) return change;
            if (!text.matches("\\d+")) return null;

            int value = Integer.parseInt(text);
            if (value < 1 || value > 3) return null;

            return change;
        };

        // Filter non-real values outside the range 1-5
        UnaryOperator<TextFormatter.Change> effortFilter = change -> {
            String text = change.getControlNewText();

            if (text.isEmpty()) return change;
            if (!text.matches("\\d*(\\.\\d*)?")) return null;

            try {
                float value = Float.parseFloat(text);
                if (value < 1 || value > 5) return null;
            } catch (NumberFormatException e) {
                return null;
            }

            return change;
        };

        // Filter non-real values
        UnaryOperator<TextFormatter.Change> timeFilter = change -> {
            String text = change.getControlNewText();

            if (text.isEmpty()) return change;
            if (!text.matches("\\d*(\\.\\d*)?")) return null;

            try {
                float value = Float.parseFloat(text);
                if (value < 0) return null;
            } catch (NumberFormatException e) {
                return null;
            }

            return change;
        };

        // Add filters to their corresponding fields
        priorityField.setTextFormatter(new TextFormatter<>(priorityFilter));
        effortField.setTextFormatter(new TextFormatter<>(effortFilter));
        timeField.setTextFormatter(new TextFormatter<>(timeFilter));
    }
}
