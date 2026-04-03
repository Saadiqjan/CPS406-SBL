// Authors: Saadiq Shahsamand, Ali Zarabi, Harjap Uppal
// Filename: BacklogController.java
// Date Created: Mar 18 2026
// Date Modified: Mar 26 2026
// Description: Controller for the backlog view.
//              Handles creation, updating, display, and deletion of product backlog items.
//              Also manages input validation and synchronization between UI and AppState.

package com.cps406.controllers;

import com.cps406.AppState;
import com.cps406.model.Item;
import com.cps406.model.ProductBacklog;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import java.util.Optional;
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

    @FXML
    private Button updateButton;

    @FXML
    private Label statusLabel;

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
     * Initializes UI components:
     * - Binds table columns to Item properties
     * - Applies input validation using TextFormatters
     * - Sets up selection listener to populate fields for editing
     */
    @FXML
    private void initialize() {
        // Initialize update button
        updateButton = null;

        //initialize backlog table
        setTableColumns(backlogTable, nameCol, priorityCol, effortCol, timeCol, riskCol);

        // Status label should not be visible or managed
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);

        // Add unary operators for the priority, effort, and time field
        addUnaryOperator(priorityField, effortField, timeField);

        // Add unary operator for risk field
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

        // Add a listener to the backlog table
        // This allows for users to select backlog items for modification
        backlogTable.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, selectedItem) -> {
            if (selectedItem != null) {
                // Retrieve attributes of selected items
                reqField.setText(selectedItem.getName());
                storyArea.setText(selectedItem.getStory());
                taskArea.setText(selectedItem.getTask());
                priorityField.setText(String.valueOf(selectedItem.getPriority()));
                effortField.setText(String.valueOf(selectedItem.getEffort()));
                timeField.setText(String.valueOf(selectedItem.getTime()));

                if (selectedItem.getRisk() >= 0) {
                    riskField.setText(String.valueOf(selectedItem.getRisk()));
                } else {
                    riskField.clear();
                }

                // Enable update button
                if (updateButton != null) {
                    updateButton.setDisable(false);
                }
            } else {
                // Disable update button
                // This is to prevent updating a null item
                if (updateButton != null) {
                    updateButton.setDisable(true);
                }
            }
        });
    }

    /**
     * Add an item to the product backlog
     */
    @FXML
    private void addToBacklog() {
        try {
            // Retrieve product backlog
            ProductBacklog pb = appState.getProductBacklog();

            // Retrieve the name, story, task, priority, effort, and risk of the item
            // from the UI text fields/areas
            Item item = getItem();

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

            // Show status method upon successful completion
            showStatusMessage("Backlog item added successfully.");

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
            alert.setHeaderText("Incorrect Format");
            alert.setContentText("""
                    Please ensure all numeric fields are filled correctly:
                    - Priority: integer (1-3)
                    - Effort: number (1-5)
                    - Time: positive number
                    - Risk: number (1-5, optional)""");

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
     */
    @FXML
    private void clearBacklog() {
        // Retrieve current backlog
        ProductBacklog pb = appState.getProductBacklog();

        // Ensure non-empty backlog
        if (!pb.getBacklog().isEmpty()) {

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
     * @param event for getting stage
     */
    @FXML
    private void goToDashboard(ActionEvent event) {
        // Load dashboard scene
        loadDashboard(event);
    }

    /**
     * Set the appstate and update table with backlog items
     * @param appState state of the app
     */
    @Override
    public void setAppState(AppState appState) {
        super.setAppState(appState);

        // Add any existing backlog items to the backlog table
        for (Item item : appState.getProductBacklog().getBacklog()) {
            backlogTable.getItems().add(item);
        }
    }

    /**
    * Updates the currently selected backlog item using form input
    */
    @FXML
    private void updateSelectedItem() {
        try {
            // Retrieve selected item
            Item selectedItem = backlogTable.getSelectionModel().getSelectedItem();

            // throw exception if the item is null
            // Ensure an item is selected before updating
            if (selectedItem == null) {
                throw new RuntimeException("Please select an item to edit.");
            }

            // Get the new item
            Item item = getItem();

            // Update the selected item
            selectedItem.setName(item.getName());
            selectedItem.setStory(item.getStory());
            selectedItem.setTask(item.getTask());
            selectedItem.setPriority(item.getPriority());
            selectedItem.setEffort(item.getEffort());
            selectedItem.setTime(item.getTime());
            selectedItem.setRisk(item.getRisk());

            // Save backlog
            appState.saveBacklog();
            backlogTable.refresh();

            // Show status method upon successful completion
            showStatusMessage("Backlog item updated successfully.");
        }
        catch (NumberFormatException nfe) {
            // Create and setup alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Incorrect Format");
            alert.setContentText("""
                    Please ensure all numeric fields are filled correctly:
                    - Priority: integer (1-3)
                    - Effort: number (1-5)
                    - Time: positive number
                    - Risk: number (1-5, optional)""");

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
    private Item getItem() {
        // Retrieve contents of text fields and areas
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
        return new Item(name, story, task, priority, effort, time, risk);
    }

    /**
     * show a status message to the user
     * @param message to be shown
     */
    private void showStatusMessage(String message) {
        // Display message
        statusLabel.setText(message);
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);

        // Keep the message up for 2 seconds and then set invisible
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> {
            statusLabel.setVisible(false);
            statusLabel.setManaged(false);
        });
        pause.play();
    }
}
