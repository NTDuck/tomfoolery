package org.tomfoolery.configurations.monolith.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

public class ContentController {

    @FXML
    private ScrollPane content;

    @FXML
    public void initialize() throws IOException {
        loadDashboard();
    }

    public void loadDashboard() throws IOException {
        loadView("Dashboard.fxml");
    }

    public void loadSettings() throws IOException {
        loadView("/fxml/ContentView.fxml");
    }

    private void loadView(String fxmlFile) throws IOException {
    }
}
