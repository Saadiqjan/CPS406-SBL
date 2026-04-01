// Authors: Saadiq Shahsamand, Ali Zarabi
// Filename: SprintController.java
// Date Created: Mar 18 2026
// Date Modified: Mar 31 2026
// Description: UI control for sprint management

package com.cps406.controllers;

import com.cps406.AppState;
import com.cps406.model.Item;
import com.cps406.model.SprintStorage;
import com.cps406.model.Storage;
import com.cps406.model.Task;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.CheckBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
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

    // Store sprint item info
    @FXML
    private Label nameLabel;

    @FXML
    private Label storyLabel;

    @FXML
    private Label sprintTaskLabel;

    @FXML
    private Label priorityLabel;

    @FXML
    private Label effortLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label riskLabel;

    // Store task table
    @FXML
    private TableView<Task> taskTable;

    @FXML
    private TableColumn<Task, Boolean> taskDoneCol;

    @FXML
    private TableColumn<Task, String> taskDescCol;

    @FXML
    private void initialize() {
        setTableColumns(sprintTable, nameCol, priorityCol, effortCol, timeCol, riskCol);

        storyLabel.setWrapText(true);
        sprintTaskLabel.setWrapText(true);

        taskDoneCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isComplete()));

        taskDescCol.setCellValueFactory(
                new PropertyValueFactory<>("description")
        );

        taskDoneCol.setCellFactory(tc -> new TableCell<Task, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    Task task = getTableView().getItems().get(getIndex());
                    sprintTable.getSelectionModel().getSelectedItem().setTaskComplete(checkBox.isSelected(), task);
                    appState.saveCurSprint();
                    taskTable.refresh();
                });
            }

            @Override
            protected void updateItem(Boolean task, boolean empty) {
                super.updateItem(task, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    Task rowTask = getTableView().getItems().get(getIndex());
                    checkBox.setSelected(rowTask.isComplete());
                    setGraphic(checkBox);
                }
            }
        });

        sprintTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selectedItem) -> {
                    if (selectedItem != null) {
                        displayItem(selectedItem);
                    }
                }
        );

        taskTable.setRowFactory(tv -> {
            TableRow<Task> row = new TableRow<>() {
                @Override
                protected void updateItem(Task task, boolean empty) {
                    super.updateItem(task, empty);

                    if (empty || task == null) {
                        setStyle("");
                    } else if (task.isComplete()) {
                        setStyle("-fx-opacity: 0.5;");
                    } else {
                        setStyle("");
                    }
                }
            };

            Tooltip tooltip = new Tooltip();
            tooltip.setWrapText(true);
            tooltip.setMaxWidth(300);

            PauseTransition delay = new PauseTransition(Duration.seconds(0.75));

            row.setOnMouseEntered(event -> {
                if (row.isEmpty() || row.getItem() == null) {
                    return;
                }

                Task task = row.getItem();

                tooltip.setText(
                        "Description:\n" + task.getDescription() + "\n\n" +
                                "Priority: " + task.getPriority() + "\n" +
                                "Effort: " + task.getEffort() + "\n" +
                                "Time: " + task.getTime()
                );

                delay.setOnFinished(e -> {
                    if (row.isHover() && !row.isEmpty()) {
                        tooltip.show(
                                row,
                                event.getScreenX() + 10,
                                event.getScreenY() + 10
                        );
                    }
                });

                delay.playFromStart();
            });

            row.setOnMouseExited(event -> {
                delay.stop();
                tooltip.hide();
            });

            row.setOnMouseMoved(event -> {
                if (tooltip.isShowing()) {
                    tooltip.setX(event.getScreenX() + 10);
                    tooltip.setY(event.getScreenY() + 10);
                }
            });

            return row;
        });

        /**
         * Create a checklist for sprint items to mark complete/incomplete
         */
        completeCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isComplete()));

        completeCol.setCellFactory(tc -> new TableCell<Item, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

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

    @FXML
    private void addTask () {
        Item item = sprintTable.getSelectionModel().getSelectedItem();

        if (item == null) {
            return;
        }

        showAddTaskPopup(item);
    }

    private void showAddTaskPopup(Item item) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Add Engineering Task");

        Label descLabel = new Label("Description:");
        TextField descField = new TextField();

        Label priorityLabel = new Label("Priority:");
        TextField priorityField = new TextField();

        Label effortLabel = new Label("Effort:");
        TextField effortField = new TextField();

        Label timeLabel = new Label("Time:");
        TextField timeField = new TextField();

        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        saveButton.setOnAction(e -> {
            String description = descField.getText().trim();
            String priorityText = priorityField.getText().trim();
            String effortText = effortField.getText().trim();
            String timeText = timeField.getText().trim();

            if (description.isEmpty() || effortText.isEmpty() || priorityText.isEmpty() || timeText.isEmpty()) {
                System.out.println("Fill required fields.");
                return;
            }

            int priority;
            float effort;
            float time;

            try {
                priority = Integer.parseInt(priorityText);
                effort = Float.parseFloat(effortText);
                time = Float.parseFloat(timeText);
            } catch (NumberFormatException ex) {
                System.out.println("Estimate must be a number.");
                return;
            }

            Task newTask = new Task(description, priority, effort, time);

            item.addTask(newTask);

            taskTable.getItems().add(newTask);

            popupStage.close();
        });

        cancelButton.setOnAction(e -> popupStage.close());

        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        VBox layout = new VBox(10,
                descLabel, descField,
                priorityLabel, priorityField,
                effortLabel, effortField,
                timeLabel, timeField,
                buttonBox
        );

        layout.setStyle("-fx-padding: 15;");
        Scene scene = new Scene(layout, 300, 400);

        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private void displayItem(Item item) {
        nameLabel.setText("Requirement: " + item.getName());
        storyLabel.setText(item.getStory());
        sprintTaskLabel.setText(item.getTask());
        priorityLabel.setText("Priority: " + item.getPriority());
        effortLabel.setText("Effort: " + item.getEffort());
        timeLabel.setText("Time: " + item.getTime() + "h");

        taskTable.getItems().clear();

        for (Task task : item.getTasks()) {
            taskTable.getItems().add(task);
        }

        if (item.getRisk() == -1) {
            riskLabel.setText("Risk: N/A");
        } else {
            riskLabel.setText("Risk: " + item.getRisk());
        }
    }

    public void setAppState(AppState appState) {
        super.setAppState(appState);

        // Add any existing backlog items to the backlog table
        for (Item item : appState.getSprintManager().getCurSprint().getItems()) {
            sprintTable.getItems().add(item);
        }
    }
}


