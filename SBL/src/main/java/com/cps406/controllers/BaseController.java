package com.cps406.controllers;

import com.cps406.AppState;

public class BaseController {
    protected AppState appState;

    public void setAppState(AppState appState) {
        this.appState = appState;
    }
}
