// Author: Saadiq Shahsamand
// Filename: DashboardController.java
// Date Created: Mar 18 2026
// Date Modified: Mar 30 2026
// Description: Controller for dashboard, allows movement to current
//              sprint and the product backlog

package com.cps406.controllers;

import com.cps406.AppState;
import com.cps406.model.Item;
import com.cps406.model.Sprint;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.*;
import javafx.event.*;

import java.io.IOException;

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
     * @param event
     * @throws IOException if loading scene fails
     */
    @FXML
    private void goToSprint(ActionEvent event) throws IOException {

        // If there is an active sprint, load the sprint scene
        // If not, load the sprint creation scene
        if (appState.getSprintManager().isActiveSprint()) {
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
        else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cps406/CreateSprint.fxml"));
            root = loader.load();

            CreateSprintController csc = loader.getController();
            csc.setAppState(appState);

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/cps406/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        }
    }

    /**
     * load and switch to backlog scene
     * @param event
     * @throws IOException if loading scene fails
     */
    @FXML
    private void goToBacklog(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cps406/Backlog.fxml"));
        root = loader.load();

        BacklogController bc = loader.getController();
        bc.setAppState(appState);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/cps406/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void setAppState(AppState appState) {
        super.setAppState(appState);

        try {
            daysAxis.setUpperBound(appState.getSprintManager().getCurSprint().getTotalDays());
            effortAxis.setUpperBound(appState.getSprintManager().getCurSprint().getTotalEffort());
            burndownChart.setAnimated(false);
            buildIdealLine();
            buildActualLine();

            sprintProgress.setProgress(appState.getSprintManager().getCurSprint().getProgress());
            progressLabel.setText((int)(sprintProgress.getProgress() * 100) + "%");
            effortLabel.setText("Remaining Effort: " + appState.getSprintManager().getCurSprint().getRemEffort());
            timeLabel.setText("Remaining Time: " + appState.getSprintManager().getCurSprint().getRemTime() + "h");
        }
        catch (NullPointerException npe) {
            sprintProgress.setProgress(0);
            progressLabel.setText("0%");
            effortLabel.setText("Remaining Effort: 0");
            timeLabel.setText("Remaining Time: 0h");
        }
    }

    private void buildIdealLine() {
        XYChart.Series<Number, Number> ideal = new XYChart.Series<>();
        ideal.setName("Ideal");
        ideal.getData().add(new XYChart.Data<>(0, appState.getSprintManager().getCurSprint().getTotalEffort()));
        ideal.getData().add(new XYChart.Data<>(appState.getSprintManager().getCurSprint().getTotalDays(), 0));
        burndownChart.getData().add(ideal);
    }

    private void buildActualLine() {
        XYChart.Series<Number, Number> actual = new XYChart.Series<>();
        actual.setName("Actual");

        // Get sprint data
        Sprint sprint = appState.getSprintManager().getCurSprint();

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
