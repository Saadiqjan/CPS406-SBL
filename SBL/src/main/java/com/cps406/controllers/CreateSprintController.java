// Filename: CreateSprintController.java
// Date Created: Mar 24 2026

package com.cps406.controllers;

import com.cps406.model.Item;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class CreateSprintController extends BaseController {

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

    /**
     * initialize cell values of each cell in the backlog table
     */
    @FXML
    private void initialize() {
        setTableColumns(pbTable, pbNameCol, pbPriorityCol, pbEffortCol, pbTimeCol, pbRiskCol);
        setTableColumns(sbTable, sbNameCol, sbPriorityCol, sbEffortCol, sbTimeCol, sbRiskCol);
    }

    /**
     * Generate a sprint backlog and display it
     */
    @FXML
    private void generateSprint() {
        // Generate list of sprint items
        ArrayList<Item> sprintList = appState.getSprintManager().generateSprintBacklog(
                appState.getProductBacklog().getBacklog(),
                20
        );

        // Add sprint items to the sprint backlog table
        for (Item sprintItem : sprintList) {
            sbTable.getItems().add(sprintItem);
        }
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
}
