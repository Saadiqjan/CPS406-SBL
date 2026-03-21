package com.cps406;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.*;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static void main (String[] args)
    { launch(args); }

    @Override
    public void start(Stage stage) throws IOException {
        AppState appState = new AppState();

        Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
