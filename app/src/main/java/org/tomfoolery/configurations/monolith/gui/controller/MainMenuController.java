package org.tomfoolery.configurations.monolith.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;

public class MainMenuController {

    @FXML
    private MenuButton profileButton;

    @FXML
    private Button notificationButton;

    @FXML
    private Button sidebarDashboard;

    @FXML
    public void initialize() {
        // Handle mouse hover event
    }

    private void openProfileOptionList() {
    }

    private void printHello() {
        System.out.println("Hello");
    }
}