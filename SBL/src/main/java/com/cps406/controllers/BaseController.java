package com.cps406.controllers;

import com.cps406.AppState;

public class BaseController {
    private AppState appState;

    public void setAppState(AppState appState) {
        this.appState = appState;
    }
}
