package org.tomfoolery.configurations.monolith.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginMenuController {
    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Button registerButton;

    @FXML
    private Label errorMessage;

    @FXML
    public void initialize() {
        loginButton.setOnAction(this::login);
        registerButton.setOnAction(this::register);
    }

    @FXML
    private void login(ActionEvent event) {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        System.out.println("Login attempt with username: " + username);
        if(!authenticate(username, password)) {
            errorMessage.setText("Invalid username or password");
            errorMessage.setVisible(true);
        }
    }

    @FXML
    private void register(ActionEvent event) {
        System.out.println("move to register scene");
    }

    private boolean authenticate(String username, String password) {
        if(username == "adnope" && password == "123456") {
            System.out.println("Login successful");
            return true;
        }
        else {
            System.out.println("Login with " + username + " failed");
            return false;
        }
    }

    private void setErrorMessage(String message) {
        errorMessage.setText(message);
    }
}
