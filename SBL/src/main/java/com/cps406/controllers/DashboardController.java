package com.cps406.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.*;
import javafx.event.*;

import java.io.IOException;

public class DashboardController extends BaseController {
    @FXML
    private void goToSprint(ActionEvent event) throws IOException {
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
}
