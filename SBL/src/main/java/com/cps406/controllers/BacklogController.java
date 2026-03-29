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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Optional;

import java.io.IOException;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

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
        setTableColumns(backlogTable, nameCol, priorityCol, effortCol, timeCol, riskCol);

        UnaryOperator<TextFormatter.Change> priorityFilter = change -> {
            String text = change.getControlNewText();

            if (text.isEmpty()) return change;
            if (!text.matches("\\d+")) return null;

            int value = Integer.parseInt(text);
            if (value < 1 || value > 3) return null;

            return change;
        };

        priorityField.setTextFormatter(new TextFormatter<>(priorityFilter));

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

            // Parse risk if not empty
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
            // Create and setup alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Incorrect Number Format");
            alert.setContentText("Please ensure all numeric fields are filled correctly:\n" +
            "- Priority: integer (1-3)\n" +
            "- Effort: number (1-5)\n" +
            "- Time: positive number\n" +
            "- Risk: number (1-5, optional)");

            alert.showAndWait();
        }
        catch (RuntimeException re) {
            // Create setup alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Adding Item");
            alert.setHeaderText("Could not add backlog item");
            alert.setContentText(re.getMessage());

            alert.showAndWait();
        }
    }

    /**
     * Clear all items from the product backlog after user confirms
     * @param event
     */
    @FXML
    private void clearBacklog(ActionEvent event) {
        // retrieve the current backlog
        ProductBacklog pb = appState.getProductBacklog();

        // make sure backlog is not empty
        if (pb.getBacklog().size() != 0) {

            // create a confirmation alert to warn the user
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Backlog Clear");
            alert.setHeaderText("Deleting all backlog items");
            alert.setContentText("Items will not be recoverable. Proceed?");

            // create button options
            ButtonType yesButton = new ButtonType("Yes");
            ButtonType cancelButton = new ButtonType("No");

            // set buttons in the alert and show the alert
            alert.getButtonTypes().setAll(yesButton, cancelButton);
            Optional<ButtonType> result = alert.showAndWait();

            // if the user confirms, clear backlog data and update UI
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
