package com.cps406.controllers;

import com.cps406.model.Item;
import com.cps406.model.ProductBacklog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

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
    private void addToBacklog(ActionEvent e) {
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
    }
}
