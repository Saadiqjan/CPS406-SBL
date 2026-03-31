// Filename: SprintController.java
// Date Created: Mar 18 2026

package com.cps406.controllers;

import com.cps406.AppState;
import com.cps406.model.Item;
import com.cps406.model.SprintStorage;
import com.cps406.model.Storage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

public class SprintController extends BaseController {
    @FXML
    private TableView<Item> sprintTable;

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

    @FXML
    private TableColumn<Item, Boolean> completeCol;

    @FXML
    private void initialize() {
        setTableColumns(sprintTable, nameCol, priorityCol, effortCol, timeCol, riskCol);

        /**
         * Create a checklist for sprint items to mark complete/incomplete
         */
        completeCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isComplete()));

        completeCol.setCellFactory(tc -> new javafx.scene.control.TableCell<Item, Boolean>() {
            private final javafx.scene.control.CheckBox checkBox = new javafx.scene.control.CheckBox();

            {
                checkBox.setOnAction(event -> {
                    Item item = getTableView().getItems().get(getIndex());

                    // mark item completed through the current sprint
                    // this allows the sprint to know how many items have been completed
                    // without going through the whole list
                    appState.getSprintManager().getCurSprint().completeItem(checkBox.isSelected(), item);

                    // Save and refresh
                    //appState.saveBacklog();
                    appState.saveCurSprint();
                    sprintTable.refresh();
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    Item rowItem = getTableView().getItems().get(getIndex());
                    checkBox.setSelected(rowItem.isComplete());
                    setGraphic(checkBox);
                }
            }
        });
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

    @FXML
    private void handleFinishSprint(ActionEvent event) throws IOException {
        appState.getSprintManager().finishSprint(appState.getProductBacklog());

        // Save after finishing
        Storage.save(appState.getProductBacklog());

        // Show confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sprint Finished");
        alert.setHeaderText(null);
        alert.setContentText("The sprint was finished successfully.");

        alert.showAndWait();

        // Go back to the dashboard
        goToDashboard(event);
    }

    public void setAppState(AppState appState) {
        super.setAppState(appState);

        // Add any existing backlog items to the backlog table
        for (Item item : appState.getSprintManager().getCurSprint().getItems()) {
            sprintTable.getItems().add(item);
        }


    }
}


