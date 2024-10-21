package org.tomfoolery.configurations.monolith.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import java.io.IOException;

public class MainMenuController {
    @FXML
    private HBox content = new HBox();

    @FXML
    private Button sidebarDashboardButton;

    @FXML
    private Button sidebarDiscoverButton;

    @FXML
    private Button notificationButton;

    @FXML
    private MenuButton profileButton;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        goToDashboard();
    }

    private void loadContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            HBox newContent = loader.load();
            content.getChildren().clear();
            content.getChildren().add(newContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void goToDashboard() {
        loadContent("/fxml/Dashboard.fxml");
    }

    @FXML
    private void goToDiscover() {
        loadContent("/fxml/Discover.fxml");
    }
}