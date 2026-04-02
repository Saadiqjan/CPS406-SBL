// Author: Saadiq Shahsamand, Harjap Uppal
// Filename: DashboardController.java
// Date Created: Mar 18 2026
// Date Modified: Apr 2 2026
// Description: Controller for dashboard, allows movement to current
//              sprint and the product backlog

package com.cps406.controllers;

import com.cps406.AppState;
import com.cps406.model.Item;
import com.cps406.model.Sprint;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.*;
import javafx.event.*;

import java.io.IOException;
import java.util.Objects;

public class DashboardController extends BaseController {

    // Store burndown chart
    @FXML
    private LineChart<Number, Number> burndownChart;

    @FXML
    private NumberAxis daysAxis;

    @FXML
    private NumberAxis effortAxis;

    // Store progress bar
    @FXML
    private ProgressBar sprintProgress;

    @FXML
    private Label progressLabel;

    // Store remaining effort and time
    @FXML
    private Label timeLabel;

    @FXML
    private Label effortLabel;

    /**
     * Load and switch to sprint scene
     * @param event for stage
     * @throws IOException if loading scene fails
     */
    @FXML
    private void goToSprint(ActionEvent event) throws IOException {

        // If there is an active sprint, load the sprint scene
        // If not, load the sprint creation scene
        if (appState.getSprintManager().isActiveSprint()) {
            loadSprint(event);
        }
        else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(createSprintPath));
            root = loader.load();

            CreateSprintController csc = loader.getController();
            csc.setAppState(appState);

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(styleSheetPath)).toExternalForm());
            stage.setScene(scene);
            stage.show();
        }
    }

    /**
     * load and switch to backlog scene
     * @param event for stage
     * @throws IOException if loading scene fails
     */
    @FXML
    private void goToBacklog(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(backlogPath));
        root = loader.load();

        BacklogController bc = loader.getController();
        bc.setAppState(appState);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(styleSheetPath)).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void setAppState(AppState appState) {
        super.setAppState(appState);

        // Setup progress bar and burn down chart
        try {
            // Set up burn down chart axis
            daysAxis.setUpperBound(appState.getSprintManager().getCurSprint().getTotalDays());
            effortAxis.setUpperBound(appState.getSprintManager().getCurSprint().getTotalEffort());
            burndownChart.setAnimated(false);

            // Build ideal and actual line
            buildIdealLine();
            buildActualLine();

            // Set the progress bar and the remaining time and effort
            sprintProgress.setProgress(appState.getSprintManager().getCurSprint().getProgress());
            progressLabel.setText((int)(sprintProgress.getProgress() * 100) + "%");
            effortLabel.setText("Remaining Effort: " + appState.getSprintManager().getCurSprint().getRemEffort());
            timeLabel.setText("Remaining Time: " + appState.getSprintManager().getCurSprint().getRemTime() + "h");
        }
        catch (NullPointerException npe) {
            // This catch block runs when there isn't an active sprint
            // Progress is 0
            sprintProgress.setProgress(0);
            progressLabel.setText("0%");
            effortLabel.setText("Remaining Effort: 0");
            timeLabel.setText("Remaining Time: 0h");
        }
    }

    /**
     * Build the ideal line of the burn down chart, which is a diagonal line.
     * It represents the amount of effort accomplished per day being evenly
     * distributed across the duration of the sprint
     */
    private void buildIdealLine() {
        //
        XYChart.Series<Number, Number> ideal = new XYChart.Series<>();
        ideal.setName("Ideal");
        ideal.getData().add(new XYChart.Data<>(0, appState.getSprintManager().getCurSprint().getTotalEffort()));
        ideal.getData().add(new XYChart.Data<>(appState.getSprintManager().getCurSprint().getTotalDays(), 0));
        burndownChart.getData().add(ideal);
    }

    /**
     * Build the actual line. This line shows the amount of effort done on each day
     * in actuality as the sprint progresses
     */
    private void buildActualLine() {
        XYChart.Series<Number, Number> actual = new XYChart.Series<>();
        actual.setName("Actual");

        // Get sprint data
        Sprint sprint = appState.getSprintManager().getCurSprint();

        // Get the total dats and effort
        int totalDays = sprint.getTotalDays();
        float totalEffort = sprint.getTotalEffort();

        // Loop through each day of the sprint
        for (int day = 0; day <= totalDays; day++) {
            float remaining = totalEffort;

            // Subtract effort of completed items up to this day
            for (Item item : sprint.getItems()) {
                if (item.isComplete() && item.getCompletionDay() != null && item.getCompletionDay() <= day) {
                    remaining -= item.getEffort();
                }
            }

            // Add data point to chart
            actual.getData().add(new XYChart.Data<>(day, remaining));
        }

        // Add series to chart
        burndownChart.getData().add(actual);
    }

}
