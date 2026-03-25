package com.cps406.controllers;

import com.cps406.AppState;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BaseController {
    protected Stage stage;
    protected Scene scene;
    protected Parent root;

    protected AppState appState;

    public void setAppState(AppState appState) {
        this.appState = appState;
    }
}
