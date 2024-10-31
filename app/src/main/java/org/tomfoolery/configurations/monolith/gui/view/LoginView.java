package org.tomfoolery.configurations.monolith.gui.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.val;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.presenter.LoginPresenter;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.dataproviders.auth.PasswordService;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.adapters.controllers.guest.auth.LogUserInController;
import org.checkerframework.checker.nullness.qual.NonNull;

public class LoginView {
    private final @NonNull LogUserInController controller;
    private final @NonNull LoginPresenter presenter;

    public LoginView(@NonNull UserRepositories userRepositories, @NonNull PasswordService passwordService, @NonNull AuthenticationTokenService authenticationTokenService,@NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.controller = LogUserInController.of(userRepositories, passwordService, authenticationTokenService, authenticationTokenRepository);
        this.presenter = LoginPresenter.of(authenticationTokenService);
    }

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

        val requestObject = LogUserInController.RequestObject.of(username, password);

        try {
            val responseModel = this.controller.apply(requestObject);
            String mainMenuFxmlPath = this.presenter.getMainMenuPathFromResponseModel(responseModel);
            StageManager.openMainMenu(mainMenuFxmlPath);
        } catch (Exception exception) {
            errorMessage.setText("Invalid username or password");
            errorMessage.setVisible(true);
        }

//        System.out.println("Login attempt with username: " + username);
//        if (authenticate(username, password)) {
//            StageManager.openMainMenu();
//        } else {
//            errorMessage.setText("Invalid username or password");
//            errorMessage.setVisible(true);
//        }
    }

    @FXML
    private void register(ActionEvent event) {
        StageManager.openSignupMenu();
    }

    private boolean authenticate(String username, String password) {
        if (username.equals("adnope") && password.equals("a")) {
            System.out.println("Login successful");
            return true;
        } else {
            System.out.println("Login with " + username + " failed");
            return false;
        }
    }
}
