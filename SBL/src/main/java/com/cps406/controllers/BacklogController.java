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
import javafx.stage.Stage;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

import java.io.IOException;

public class BacklogController extends BaseController {
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
    private TextField riskField;

    @FXML
    private TableView<Item> backlogTable;

    @FXML
    private TableColumn<Item, String> nameCol;

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
    private TableColumn<Item, Float> riskCol;

    @FXML
    private void initialize() {
        nameCol.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );

        priorityCol.setCellValueFactory(
                new PropertyValueFactory<>("priority")
        );

        effortCol.setCellValueFactory(
                new PropertyValueFactory<>("effort")
        );

        riskCol.setCellValueFactory(
                new PropertyValueFactory<>("risk")
        );
    }

    @FXML
    private void addToBacklog(ActionEvent event) {
        try {
            ProductBacklog pb = appState.getProductBacklog();

            String name = reqField.getText().trim();
            String story = storyArea.getText().trim();
            String task = taskArea.getText().trim();

            int priority = Integer.parseInt(priorityField.getText().trim());
            float effort = Float.parseFloat(effortField.getText().trim());
            float risk = Float.parseFloat(riskField.getText().trim());

            Item item = new Item(name, story, task, priority, effort, risk);
            pb.addItem(item);
            backlogTable.getItems().add(item);
            appState.saveBacklog();

            reqField.clear();
            storyArea.clear();
            taskArea.clear();
            priorityField.clear();
            effortField.clear();
            riskField.clear();
        }
        catch (NumberFormatException nfe) {

        }
        catch (Exception e) {

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

    @FXML
    private void goToDashboard(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cps406/Dashboard.fxml"));
        root = loader.load();

        DashboardController dbc = loader.getController();
        dbc.setAppState(appState);

        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/cps406/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void setAppState(AppState appState) {
        super.setAppState(appState);

        for (Item item : appState.getProductBacklog().getBacklog()) {
            backlogTable.getItems().add(item);
        }
    }
}
