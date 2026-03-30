// Authors: Saadiq Shahsamand
// Filename: CreateSprintController.java
// Date Created: Mar 24 2026
// Date Modified: Mar 29 2026
// Description: Controller for sprint creation scene.
//              Lets you generate a list for the upcoming sprint
//              and allows for modification of that list before approval

package com.cps406.controllers;

import com.cps406.AppState;
import com.cps406.model.Item;
import com.cps406.model.SprintManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;

public class CreateSprintController extends BaseController {
    // Store labels
    @FXML
    private Label capacityLabel;

    // Store text fields
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

    // Store capacity (to avoid recalculation
    int capacity = 0;
    float totalTime = 0.0f;

    //Store table lists
    ObservableList<Item> pbItems = FXCollections.observableArrayList();
    ObservableList<Item> sbItems    = FXCollections.observableArrayList();

    /**
     * initialize cell values of each cell in the backlog table
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

        // Create a
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

        // Add listeners to the number of devs and duration textfields
        // These listeners will recalculate the sprint capacity when either is changed
        numDevField.textProperty().addListener((obs, old, newVal) -> {
            updateCapacity(numDevField, newVal);
        });
        durationField.textProperty().addListener((obs, old, newVal) -> {
            updateCapacity(durationField, newVal);
        });
    }

    /**
     * Update the total time of the sprint table
     */
    private void updateTotalTime() {
        // Reset total time
        totalTime = 0.0f;

        // for each item add its time to the total
        for (Item item : sbItems) {
            totalTime += item.getTime();
        }

        // Set the capacity label to match this
        capacityLabel.setText("Capacity " + String.format("%.2f", totalTime)  + "/" + capacity);
    }

    /**
     * update the capacity of the sprint
     * @param source which textfield the new value came from
     * @param newVal the new value in the textfield
     */
    private void updateCapacity(TextField source, String newVal) {
        try {
            // Start with the new value
            capacity = Integer.parseInt(newVal);

            // multiply by the value from the unchanged field and multiply by 30
            // if you are wondering why 30:
            // Capacity = # of devs * # of working hours
            // We assume 30 working hours in a work week to account for overhead, that's 6/8 hours a day
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
            // TODO: replace with more robust logging
            nfe.printStackTrace();
        }
    }

    /**
     * move an item from the product backlog to the sprint backlog
     * @param item to be added
     */
    private void addToSprint(Item item) {
        // Prevent removing an item that isnt there
        // If remove fails and add doesnt it will lead to the creation or duplication of an item
        if (!pbItems.contains(item)) return;

        // Move the item
        pbItems.remove(item);
        sbItems.add(item);

        // Updaye the total time to match
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
     * Generate a sprint backlog and display it
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
     * create the sprint
     * @param event
     */
    @FXML
    private void createSprint(ActionEvent event) {
        // Retrieve the sprint manager and the items selected for the sprint
        SprintManager sm = appState.getSprintManager();
        ArrayList<Item> selectedItems = new ArrayList<>(sbItems);
        int duration = 0;

        // Prevent the creation of a sprint with no items
        if (selectedItems.isEmpty()) {
            return;
        }

        try {
            // Retrieve the duration of the sprint
            duration = Integer.parseInt(durationField.getText());

            // Create a new sprint
            // If the creation was successful, switch to the sprint scene
            if (sm.createSprint(capacity, LocalDate.now().plusWeeks(duration), appState.getProductBacklog(), selectedItems)) {
                appState.saveSprintBacklog();
                appState.saveBacklog();
                goToSprint(event);
            }
        }
        catch (NumberFormatException nfe) {

        }
        catch (IOException ioe) {

        }
    }

    /**
     * Clear the sprint table
     */
    @FXML
    private void clearSprintTable() {
        // Remove every item in the table
        new ArrayList<>(sbItems).forEach(item -> removeFromSprint(item));

        // Update total time to match
        updateTotalTime();
    }

    /**
     * Go to dashboard
     * @param event
     * @throws IOException if IO fails
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

    /**
     * Go to sprint
     * @param event
     * @throws IOException if IO fails
     */
    private void goToSprint(ActionEvent event) throws IOException {
        // Load sprint scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cps406/Sprint.fxml"));
        root = loader.load();

        // Set the app state of the controller for the sprint scene
        SprintController sc = loader.getController();
        sc.setAppState(appState);

        // Create and set the scene
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/cps406/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Set the appstate
     * @param appState
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
