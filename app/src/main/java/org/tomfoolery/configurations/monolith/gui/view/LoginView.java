package org.tomfoolery.configurations.monolith.gui.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.val;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.infrastructures.adapters.controllers.guest.auth.LogUserInController;

public class LoginView {
    private final @NonNull LogUserInController controller;

    public LoginView(@NonNull UserRepositories userRepositories,
                     @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
                     @NonNull AuthenticationTokenRepository authenticationTokenRepository,
                     @NonNull PasswordEncoder passwordEncoder) {
        this.controller = LogUserInController.of(
                userRepositories,
                authenticationTokenGenerator,
                authenticationTokenRepository,
                passwordEncoder
        );
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
        char[] passwordCharArray = password.toCharArray();

        val requestObject = LogUserInController.RequestObject.of(username, passwordCharArray);

        try {
            val viewModel = this.controller.apply(requestObject);
            onSuccess(viewModel);
        } catch (Exception exception) {
            errorMessage.setText("Invalid username or password");
            errorMessage.setVisible(true);
        }
    }

    private void onSuccess(LogUserInController.ViewModel viewModel) {
        val userType = viewModel.getUserType();

        if (userType.equals(LogUserInController.UserType.ADMINISTRATOR)) {
            StageManager.getInstance().loadAdminView(StageManager.ContentType.ADMIN_DASHBOARD);
        }
        else if (userType.equals(LogUserInController.UserType.STAFF)) {
            StageManager.getInstance().loadStaffView(StageManager.ContentType.STAFF_DASHBOARD);
        }
        else StageManager.getInstance().loadStaffView(StageManager.ContentType.STAFF_DASHBOARD);
    }

    @FXML
    private void register(ActionEvent event) {
        StageManager.getInstance().openSignupMenu();
    }
}

