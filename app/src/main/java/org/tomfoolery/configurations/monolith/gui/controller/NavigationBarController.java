package org.tomfoolery.configurations.monolith.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NavigationBarController {
    @FXML
    private TextField searchField;

    @FXML
    private Button notificationButton;

    @FXML
    private MenuButton profileButton;

    @FXML
    public void initialize() {
        searchField.setText("");
    }

    @FXML
    private void openLoginMenu() {
    }

    @FXML
    private void printSomething() {
    }
}
