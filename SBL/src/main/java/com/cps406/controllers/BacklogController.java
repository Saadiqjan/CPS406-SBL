// Authors: Saadiq Shahsamand, Ali Zarabi
// Filename: BacklogController.java
// Date Created: Mar 18 2026
// Date Modified: Mar 26 2026
// Description: Controller for the backlog scene, allows creation
//              of backlog items and clearing the backlog

package com.cps406.controllers;

import com.cps406.AppState;
import com.cps406.model.Item;
import com.cps406.model.ProductBacklog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

import java.io.IOException;

import javafx.animation.PauseTransition;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TableRow;
import javafx.util.Duration;

import javafx.scene.control.TextFormatter;
import java.util.function.UnaryOperator;

public class BacklogController extends BaseController {

    // Store field and areas for user input
    // Users will enter details of items they want to add in these fields and areas
    @FXML
    private TextField reqField;

    @FXML
    private TextArea storyArea;

    @FXML
    private TextArea taskArea;

    @FXML
    private TextField priorityField;

    @FXML
    private TextField effortField;

    @FXML
    private TextField timeField;

    @FXML
    private TextField riskField;

    // Store the table for viewing the list of backlog items
    @FXML
    private TableView<Item> backlogTable;

    // Store the columns in the backlog table
    @FXML
    private TableColumn<Item, String> nameCol;

    // TODO: add space in the backlog table for the story and task
//    @FXML
//    private TableColumn<Item, String>
//
//    @FXML
//    private TableColumn<Item, String>

    @FXML
    private TableColumn<Item, Integer> priorityCol;

    @FXML
    private TableColumn<Item, Float> effortCol;

    @FXML
    private TableColumn<Item, Float> timeCol;

    @FXML
    private TableColumn<Item, Float> riskCol;

    /**
     * initialize cell values of each cell in the backlog table
     */
    @FXML
    private void initialize() {
        nameCol.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );

        priorityCol.setCellValueFactory(
                new PropertyValueFactory<>("priority")
        );

        UnaryOperator<TextFormatter.Change> priorityFilter = change -> {
            String text = change.getControlNewText();

            if (text.isEmpty()) return change;
            if (!text.matches("\\d+")) return null;

            int value = Integer.parseInt(text);
            if (value < 1 || value > 3) return null;

            return change;
        };

        priorityField.setTextFormatter(new TextFormatter<>(priorityFilter));

        effortCol.setCellValueFactory(
                new PropertyValueFactory<>("effort")
        );

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

        effortField.setTextFormatter(new TextFormatter<>(effortFilter));

        timeCol.setCellValueFactory(
                new PropertyValueFactory<>("time")
        );

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

        timeField.setTextFormatter(new TextFormatter<>(timeFilter));

        riskCol.setCellValueFactory(
                new PropertyValueFactory<>("risk")
        );

        UnaryOperator<TextFormatter.Change> riskFilter = change -> {
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

        riskField.setTextFormatter(new TextFormatter<>(riskFilter));

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

    /**
     * Add an item to the product backlog
     * @param event
     */
    @FXML
    private void addToBacklog(ActionEvent event) {
        try {
            // Retrieve product backlog
            ProductBacklog pb = appState.getProductBacklog();

            // Retrieve the name, story, task, priority, effort, and risk of the item
            // from the UI text fields/areas
            String name = reqField.getText().trim();
            String story = storyArea.getText().trim();
            String task = taskArea.getText().trim();
            int priority = Integer.parseInt(priorityField.getText().trim());
            float effort = Float.parseFloat(effortField.getText().trim());
            float time = Float.parseFloat(timeField.getText().trim());

            String tempRisk = riskField.getText().trim();
            float risk = -1;

            // Parse risk if not
            if (!tempRisk.isEmpty()) {
                risk = Float.parseFloat(tempRisk);
            }


            // Throw exception if name, story, or task is empty
            if (name.isEmpty() || story.isEmpty() || task.isEmpty()) {
                throw new RuntimeException("Empty string");
            }

            // Create new item with the retrieved values
            Item item = new Item(name, story, task, priority, effort, time, risk);

            // Add item to product backlog and backlog table
            // throw error if the item with same name already exists
            if (pb.addItem(item)) {
                backlogTable.getItems().add(item);
            }
            else {
                throw new RuntimeException("Item with this name already exists");
            }

            // Save backlog
            appState.saveBacklog();

            // At this point backlog addition was a success
            // Clear the text fields and areas in the UI
            reqField.clear();
            storyArea.clear();
            taskArea.clear();
            priorityField.clear();
            effortField.clear();
            timeField.clear();
            riskField.clear();
        }
        catch (NumberFormatException nfe) {
            // TODO: display error message to user
        }
        catch (RuntimeException re) {
            // TODO: same here
        }
    }

    @FXML
    private void clearBacklog(ActionEvent event) {
        ProductBacklog pb = appState.getProductBacklog();

        if (pb.getBacklog().size() != 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Backlog Clear");
            alert.setHeaderText("Deleting all backlog items");
            alert.setContentText("Items will not be recoverable. Proceed?");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType cancelButton = new ButtonType("No");

            alert.getButtonTypes().setAll(yesButton, cancelButton);
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == yesButton) {
                appState.getProductBacklog().clearBacklog();
                backlogTable.getItems().clear();
                appState.saveBacklog();
            }
        }
    }

    /**
     * Load and switch to the dashboard scene
     * @param event
     * @throws IOException if loading in the dashboard scene fails
     */
    @FXML
    private void goToDashboard(ActionEvent event) throws IOException {
        // Load dashboard scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cps406/Dashboard.fxml"));
        root = loader.load();

        // Set the app state of the controller for the dashboard scene
        DashboardController dbc = loader.getController();
        dbc.setAppState(appState);

        // Create and set the scene
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/cps406/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    // Set the app state
    @Override
    public void setAppState(AppState appState) {
        super.setAppState(appState);

        // Add any existing backlog items to the backlog table
        for (Item item : appState.getProductBacklog().getBacklog()) {
            backlogTable.getItems().add(item);
        }
    }
}
