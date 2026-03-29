// Filename: CreateSprintController.java
// Date Created: Mar 24 2026

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
        pbTable.setItems(pbItems);
        sbTable.setItems(sbItems);

        setTableColumns(pbTable, pbNameCol, pbPriorityCol, pbEffortCol, pbTimeCol, pbRiskCol);
        setTableColumns(sbTable, sbNameCol, sbPriorityCol, sbEffortCol, sbTimeCol, sbRiskCol);

        addCol.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue())
        );

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

        removeCol.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue())
        );

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

        numDevField.textProperty().addListener((obs, old, newVal) -> {
            updateCapacity(numDevField, newVal);
        });

        durationField.textProperty().addListener((obs, old, newVal) -> {
            updateCapacity(durationField, newVal);
        });
    }

    private void updateTotalTime() {
        totalTime = 0.0f;

        for (Item item : sbTable.getItems()) {
            totalTime += item.getTime();
        }

        capacityLabel.setText("Capacity " + totalTime + "/" + capacity);
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

            capacityLabel.setText("Capacity " + totalTime + "/" + capacity);
        }
        catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
    }

    private void addToSprint(Item item) {
        if (!pbItems.contains(item)) return;
        pbItems.remove(item);
        sbItems.add(item);
        updateTotalTime();
    }

    private void removeFromSprint(Item item) {
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
        totalTime = 0.0f;

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
