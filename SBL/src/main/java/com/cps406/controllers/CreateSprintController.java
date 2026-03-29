// Filename: CreateSprintController.java
// Date Created: Mar 24 2026

package com.cps406.controllers;

import com.cps406.AppState;
import com.cps406.model.Item;
import com.cps406.model.SprintManager;
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

    // Store capacity (to avoid recalculation
    int capacity = 0;

    /**
     * initialize cell values of each cell in the backlog table
     */
    @FXML
    private void initialize() {
        setTableColumns(pbTable, pbNameCol, pbPriorityCol, pbEffortCol, pbTimeCol, pbRiskCol);
        setTableColumns(sbTable, sbNameCol, sbPriorityCol, sbEffortCol, sbTimeCol, sbRiskCol);

        numDevField.textProperty().addListener((obs, old, newVal) -> {
            updateCapacity(numDevField, newVal);
        });

        durationField.textProperty().addListener((obs, old, newVal) -> {
            updateCapacity(durationField, newVal);
        });
    }

    private void updateCapacity(TextField source, String newVal) {
        try {
            capacity = Integer.parseInt(newVal);

            if (source == numDevField) {
                capacity *= Integer.parseInt(durationField.getText()) * 30;
            }
            else if (source == durationField) {
                capacity *= Integer.parseInt(numDevField.getText()) * 30;
            }

            capacityLabel.setText("Capacity 0/" + capacity);
        }
        catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
    }

    /**
     * Generate a sprint backlog and display it
     */
    @FXML
    private void generateSprint() {
        float totalTime = 0.0f;

        // Generate list of sprint items
        ArrayList<Item> sprintList = appState.getSprintManager().generateSprintBacklog(
                appState.getProductBacklog().getBacklog(),
                capacity
        );

        // Add sprint items to the sprint backlog table
        sbTable.getItems().clear();
        for (Item sprintItem : sprintList) {
            sbTable.getItems().add(sprintItem);
            totalTime += sprintItem.getTime();
        }

        // Update the backlog
        pbTable.getItems().clear();
        for (Item item : appState.getProductBacklog().getBacklog()) {
            if (!sprintList.contains(item)) {
                pbTable.getItems().add(item);
            }
        }

        // Update capacity label
        capacityLabel.setText("Capacity " + totalTime + "/" + capacity);
    }

    @FXML
    private void createSprint(ActionEvent event) {
        SprintManager sm = appState.getSprintManager();
        ArrayList<Item> selectedItems = new ArrayList<Item>(sbTable.getItems());
        int duration = 0;

        if (selectedItems.isEmpty()) {
            return;
        }

        try {
            duration = Integer.parseInt(durationField.getText());

            if (sm.createSprint(capacity, LocalDate.now().plusWeeks(duration), appState.getProductBacklog(), selectedItems)) {
                goToSprint(event);
            }
        }
        catch (NumberFormatException nfe) {

        }
        catch (IOException ioe) {

        }
    }

    @FXML
    private void clearSprintTable() {
        for (Item item : sbTable.getItems()) {
            pbTable.getItems().add(item);
        }

        sbTable.getItems().clear();
        capacityLabel.setText("Capacity 0/" + capacity);
    }

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

    private void goToSprint(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cps406/Sprint.fxml"));
        root = loader.load();

        SprintController sc = loader.getController();
        sc.setAppState(appState);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/cps406/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void setAppState(AppState appState) {
        super.setAppState(appState);

        // Add any existing backlog items to the backlog table
        for (Item item : appState.getProductBacklog().getBacklog()) {
            pbTable.getItems().add(item);
        }
    }
}
