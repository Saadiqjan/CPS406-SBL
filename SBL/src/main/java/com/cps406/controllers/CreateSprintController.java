// Authors: Saadiq Shahsamand, Harjap Uppal, Ali Zarabi
// Filename: CreateSprintController.java
// Date Created: Mar 24 2026
// Date Modified: Mar 29 2026
// Description: Controller for the sprint creation view.
//              Allows users to generate, modify, and finalize a sprint backlog
//              based on available capacity and product backlog items.

package com.cps406.controllers;

import com.cps406.AppState;
import com.cps406.Main;
import com.cps406.model.Item;
import com.cps406.model.SprintManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateSprintController extends BaseController {
    // Displays current sprint capacity usage (time used vs total capacity)
    @FXML
    private Label capacityLabel;

    // Input fields for sprint modifications
    @FXML
    private TextField durationField;

    @FXML
    private TextField numDevField;

    // Store the columns in the product backlog table
    @FXML
    private TableView<Item> pbTable;

    @FXML
    private TableColumn<Item, String> pbNameCol;

    @FXML
    private TableColumn<Item, Integer> pbPriorityCol;

    @FXML
    private TableColumn<Item, Float> pbEffortCol;

    @FXML
    private TableColumn<Item, Float> pbTimeCol;

    @FXML
    private TableColumn<Item, Float> pbRiskCol;

    @FXML
    private TableColumn<Item, Item> addCol;

    // Store the columns in the sprint backlog table
    @FXML
    private TableView<Item> sbTable;

    @FXML
    private TableColumn<Item, String> sbNameCol;

    @FXML
    private TableColumn<Item, Integer> sbPriorityCol;

    @FXML
    private TableColumn<Item, Float> sbEffortCol;

    @FXML
    private TableColumn<Item, Float> sbTimeCol;

    @FXML
    private TableColumn<Item, Float> sbRiskCol;

    @FXML
    private TableColumn<Item, Item> removeCol;

    // Store capacity (to avoid recalculation)
    int capacity = 0;
    float totalTime = 0.0f;

    //Store table lists
    ObservableList<Item> pbItems = FXCollections.observableArrayList();
    ObservableList<Item> sbItems    = FXCollections.observableArrayList();

    /**
     * Initializes tables, column bindings, button cells, and input listeners.
     * Sets up interaction between product backlog and sprint backlog tables.
     */
    @FXML
    private void initialize() {
        // Set table lists to the two tables
        pbTable.setItems(pbItems);
        sbTable.setItems(sbItems);

        // Set the table columns
        setTableColumns(pbTable, pbNameCol, pbPriorityCol, pbEffortCol, pbTimeCol, pbRiskCol);
        setTableColumns(sbTable, sbNameCol, sbPriorityCol, sbEffortCol, sbTimeCol, sbRiskCol);

        // Set the add and remove column values to the row it is in itself
        // This is necessary for retrieving the row when the add or remove button is pressed
        addCol.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue())
        );
        removeCol.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue())
        );

        // Add "+" button to move items into sprint backlog
        addCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("+");

            {
                btn.setOnAction(e -> {
                    Item item = getItem();
                    Platform.runLater(() -> addToSprint(item));
                });
            }

            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Add "×" button to remove items from sprint backlog
        removeCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("×");

            {
                btn.setOnAction(e -> {
                    Item item = getItem();
                    Platform.runLater(() -> removeFromSprint(item));
                });
            }

            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Add listeners to the number of devs and duration text fields
        // These listeners will recalculate the sprint capacity when either is changed
        numDevField.textProperty().addListener((obs, old, newVal) -> updateCapacity(numDevField, newVal));
        durationField.textProperty().addListener((obs, old, newVal) -> updateCapacity(durationField, newVal));
    }

    /**
     * Recalculates total estimated time of sprint items
     * and updates capacity display
     */
    private void updateTotalTime() {
        // Reset total time
        totalTime = 0.0f;

        // for each item add it's time to the total
        for (Item item : sbItems) {
            totalTime += item.getTime();
        }

        // Set the capacity label to match this
        capacityLabel.setText("Capacity " + String.format("%.2f", totalTime)  + "/" + capacity);
    }

    /**
     * Recalculates sprint capacity based on number of developers
     * and sprint duration.
     * Capacity = developers × weeks × 30 hours (adjusted for overhead)
     *
     * @param source which textfield the new value came from
     * @param newVal the new value in the textfield
     */
    private void updateCapacity(TextField source, String newVal) {
        try {
            // Start with the new value
            capacity = Integer.parseInt(newVal);

            // Combine with other input field and apply weekly capacity factor (30 hours)
            if (source == numDevField) {
                capacity *= Integer.parseInt(durationField.getText()) * 30;
            }
            else if (source == durationField) {
                capacity *= Integer.parseInt(numDevField.getText()) * 30;
            }

            // Update the capacity label to match
            capacityLabel.setText("Capacity " + String.format("%.2f", totalTime) + "/" + capacity);
        }
        catch (NumberFormatException nfe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.WARNING, "capacity update failed", nfe);
        }
    }

    /**
     * move an item from the product backlog to the sprint backlog
     * @param item to be added
     */
    private void addToSprint(Item item) {
        // Prevent removing an item that isn't there
        // If remove fails and add doesn't it will lead to the creation or duplication of an item
        if (!pbItems.contains(item)) return;

        // Move the item
        pbItems.remove(item);
        sbItems.add(item);

        // Update the total time to match
        updateTotalTime();
    }

    /**
     * move an item from the sprint backlog back to the product backlog
     * @param item to be removed
     */
    private void removeFromSprint(Item item) {
        // Similar to above
        if (!sbItems.contains(item)) return;
        sbItems.remove(item);
        pbItems.add(item);
        updateTotalTime();
    }

    /**
     * Generates an optimized sprint backlog based on capacity
     * and updates the UI accordingly
     */
    @FXML
    private void generateSprint() {
        // Generate list of sprint items
        ArrayList<Item> sprintList = appState.getSprintManager().generateSprintBacklog(
                appState.getProductBacklog().getBacklog(),
                capacity
        );

        // Clear current sprint back to PB first
        pbItems.addAll(sbItems);
        sbItems.clear();

        // Move generated items from PB to sprint
        sprintList.forEach(item -> {
            pbItems.remove(item);
            sbItems.add(item);
        });

        // Update capacity label
        updateTotalTime();
    }

    /**
     * Finalizes sprint creation and transitions to sprint view
     * @param event for stage
     */
    @FXML
    private void createSprint(ActionEvent event) {
        // Retrieve the sprint manager and the items selected for the sprint
        SprintManager sm = appState.getSprintManager();
        ArrayList<Item> selectedItems = new ArrayList<>(sbItems);
        int duration;

        // Prevent the creation of a sprint with no items
        if (selectedItems.isEmpty()) {
            return;
        }

        try {
            // Retrieve the duration of the sprint
            duration = Integer.parseInt(durationField.getText());

            // Create a new sprint
            // If the creation was successful, switch to the sprint scene
            if (sm.createSprint(capacity, LocalDate.now().plusWeeks(duration), duration, appState.getProductBacklog(), selectedItems)) {
                appState.saveCurSprint();
                appState.saveBacklog();
                goToSprint(event);
            }
        }
        catch (NumberFormatException nfe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.WARNING, "Failed to retrieve sprint duration", nfe);

            new Alert(Alert.AlertType.ERROR, "Invalid sprint duration")
                    .showAndWait();
        }
        catch (IOException ioe) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.WARNING, "Saving app state failed", ioe);
        }
    }

    /**
     * Moves all items back to product backlog and clears sprint selection
     */
    @FXML
    private void clearSprintTable() {
        // Remove every item in the table
        new ArrayList<>(sbItems).forEach(this::removeFromSprint);

        // Update total time to match
        updateTotalTime();
    }

    /**
     * Go to dashboard
     * @param event for stage
     */
    @FXML
    private void goToDashboard(ActionEvent event) {
        // Load dashboard scene
        loadDashboard(event);
    }

    /**
     * Go to sprint
     * @param event for stage
     * @throws IOException if IO fails
     */
    private void goToSprint(ActionEvent event) throws IOException {
        // Load sprint scene
        loadSprint(event);
    }

    /**
     * Set the appstate and update product backlog table
     * @param appState the state of the app
     */
    @Override
    public void setAppState(AppState appState) {
        super.setAppState(appState);

        // Add any existing backlog items to the backlog table
        for (Item item : appState.getProductBacklog().getBacklog()) {
            pbTable.getItems().add(item);
        }
    }
}
