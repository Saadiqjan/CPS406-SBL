// Authors: Saadiq Shahsamand, Ali Zarabi
// Filename: SprintController.java
// Date Created: Mar 18 2026
// Date Modified: Apr 1 2026
// Description: UI control for sprint management

package com.cps406.controllers;

import com.cps406.AppState;
import com.cps406.model.Item;
import com.cps406.model.Storage;
import com.cps406.model.Task;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
        // Set up table columns of the sprint
        setTableColumns(sprintTable, nameCol, priorityCol, effortCol, timeCol, riskCol);

        // Configure layout of labels
        // Prevents labels displaying story and task of a label
        // from getting cut off
        storyLabel.setWrapText(true);
        sprintTaskLabel.setWrapText(true);

        // Configure task done and task description columns
        taskDoneCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isComplete()));

        taskDescCol.setCellValueFactory(
                new PropertyValueFactory<>("description")
        );

        // Non-empty tasks should have a checkbox to mark the task complete
        taskDoneCol.setCellFactory(tc -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                // Check box should mark or unmark a task for completion
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

        // Add a listener to the sprint table to detect when a sprint item is selected
        sprintTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selectedItem) -> {
                    if (selectedItem != null) {
                        displayItem(selectedItem);
                    }
                }
        );

        // Configure task table
        // Creates a tooltip for a task when you hover over it
        // This tool tip displays information about the task
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

        // Create a checklist for sprint items to mark complete/incomplete
        completeCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isComplete()));

        completeCol.setCellFactory(tc -> new TableCell<>() {
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

    /**
     * go to dashboard
     * @param event for stage
     */
    @FXML
    private void goToDashboard(ActionEvent event) {
        // Load dashboard scene
        loadDashboard(event);
    }

    /**
     * Complete a sprint, the sprint should be recorded and
     * incomplete items should be returned to the product backlog
     * @param event for stage
     */
    @FXML
    private void handleFinishSprint(ActionEvent event) {
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

    /**
     * add a task to a sprint item
     */
    @FXML
    private void addTask () {
        // Retrieve sprint item
        Item item = sprintTable.getSelectionModel().getSelectedItem();

        // Do nothing if the item is null
        if (item == null) {
            return;
        }

        // Prompt user with a popup to add the task
        showAddTaskPopup(item);
    }

    /**
     * popup that allows user to fill in fields to add a task to a sprint item
     * @param item for popup
     */
    private void showAddTaskPopup(Item item) {
        // Create stage for popup
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Add Engineering Task");

        // Create fields that the user needs to fill out
        Label descLabel = new Label("Description:");
        TextField descField = new TextField();

        Label priorityLabel = new Label("Priority:");
        TextField priorityField = new TextField();
        priorityField.setPromptText("(1-3)");

        Label effortLabel = new Label("Effort:");
        TextField effortField = new TextField();
        effortField.setPromptText("(1-5)");

        Label timeLabel = new Label("Time:");
        TextField timeField = new TextField();
        timeField.setPromptText("(hours)");

        // add filters to certain fields to ensure correct input
        addUnaryOperator(priorityField, effortField, timeField);

        // Create the button to save and add task
        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        // Set action for save button
        saveButton.setOnAction(e -> {
            // Retrieve field values
            String description = descField.getText().trim();
            String priorityText = priorityField.getText().trim();
            String effortText = effortField.getText().trim();
            String timeText = timeField.getText().trim();

            // Attempt to parse integer and float values
            int priority;
            float effort;
            float time;

            // Catch any errors
            try {
                if (description.isEmpty() || effortText.isEmpty() || priorityText.isEmpty() || timeText.isEmpty()) {
                    throw new NumberFormatException();
                }

                priority = Integer.parseInt(priorityText);
                effort = Float.parseFloat(effortText);
                time = Float.parseFloat(timeText);
            } catch (NumberFormatException ex) {
                // Create and setup alert
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setHeaderText("Incorrect Format");
                alert.setContentText("""
                        Please ensure all numeric fields are filled correctly:
                        - Priority: integer (1-3)
                        - Effort: number (1-5)
                        - Time: positive number""");

                alert.showAndWait();
                return;
            }

            // Create new task
            Task newTask = new Task(description, priority, effort, time);

            // Add task to the sprint item
            item.addTask(newTask);

            // Add task to task table
            taskTable.getItems().add(newTask);

            // Save the sprint and close popup window
            appState.saveCurSprint();
            popupStage.close();
        });

        // Close popup upon cancellation
        cancelButton.setOnAction(e -> popupStage.close());

        // Configure stage layout
        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        VBox layout = new VBox(10,
                descLabel, descField,
                priorityLabel, priorityField,
                effortLabel, effortField,
                timeLabel, timeField,
                buttonBox
        );

        // Set scene
        layout.setStyle("-fx-padding: 15;");
        Scene scene = new Scene(layout, 300, 400);

        // Display popup
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    /**
     * Display and item when it is selected from the sprint backlog table
     * @param item to be displayed
     */
    private void displayItem(Item item) {
        // Retrieve item attributes and display
        // Risk is N/A if negative
        nameLabel.setText("Requirement: " + item.getName());
        storyLabel.setText(item.getStory());
        sprintTaskLabel.setText(item.getTask());
        priorityLabel.setText("Priority: " + item.getPriority());
        effortLabel.setText("Effort: " + item.getEffort());
        timeLabel.setText("Time: " + item.getTime() + "h");
        if (item.getRisk() == -1) {
            riskLabel.setText("Risk: N/A");
        } else {
            riskLabel.setText("Risk: " + item.getRisk());
        }

        // Clear the task table
        taskTable.getItems().clear();

        // Add selected item's tasks to task table
        for (Task task : item.getTasks()) {
            taskTable.getItems().add(task);
        }
    }

    /**
     * Set the app state
     * @param appState the app state
     *
     */
    public void setAppState(AppState appState) {
        super.setAppState(appState);

        // Add any existing backlog items to the backlog table
        for (Item item : appState.getSprintManager().getCurSprint().getItems()) {
            sprintTable.getItems().add(item);
        }
    }
}


