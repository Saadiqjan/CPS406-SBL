package com.cps406;

import com.cps406.controllers.DashboardController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.IOException;

public class Main extends Application {
    public static void main (String[] args)
    { launch(args); }

    @Override
    public void start(Stage stage) throws IOException {
        AppState appState = new AppState();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        DashboardController dbc = loader.getController();
        dbc.setAppState(appState);

        stage.setScene(scene);
        stage.setTitle("Product Backlog Manager");
        stage.getIcons().add(
                new Image(getClass().getResourceAsStream("/com/cps406/logo64.png")));
        stage.show();
    }
}
