// Author: Saadiq Shahsamand
// Filename: BaseController.java
// Date Created: Mar 24 2026
// Date Modified: Mar 25 2026
// Description: Controller super class that contains methods and attributes
//              all controls will require

package com.cps406.controllers;

import com.cps406.AppState;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BaseController {
    // Store stage scene and root
    // Required for switching between scenes
    protected Stage stage;
    protected Scene scene;
    protected Parent root;

    // Store the app state of the program
    // App state contains the product backlog and sprint manager
    protected AppState appState;

    // Set app state
    public void setAppState(AppState appState) {
        this.appState = appState;
    }
}
