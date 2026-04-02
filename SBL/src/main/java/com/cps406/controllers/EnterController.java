// Author: Ali Zarabi, Saadiq Shahsamand
// Filename: EnterController
// Creation Date: Mar 26 2026
// Modified Date: Apr 1 2026
// Description: entry page before entering the dashboard

package com.cps406.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class EnterController extends BaseController{
    /**
     * load dashboard
     * @param event for stage
     */
    @FXML
    private void goToDashboard(ActionEvent event) {
        loadDashboard(event);
    }
}
