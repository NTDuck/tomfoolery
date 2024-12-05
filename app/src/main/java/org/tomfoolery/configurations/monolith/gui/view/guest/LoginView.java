package org.tomfoolery.configurations.monolith.gui.view.guest;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.val;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.utils.MessageLabelFactory;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.guest.users.authentication.LogUserInByCredentialsUseCase;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.infrastructures.adapters.controllers.guest.users.authentication.LogUserInByCredentialsController;

public class LoginView {
    private final @NonNull LogUserInByCredentialsController controller;

    public LoginView(@NonNull UserRepositories userRepositories,
                     @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
                     @NonNull AuthenticationTokenRepository authenticationTokenRepository,
                     @NonNull PasswordEncoder passwordEncoder) {
        this.controller = LogUserInByCredentialsController.of(
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
        passwordTextField.setOnAction(this::login);
        usernameTextField.setOnAction(this::login);
        errorMessage.setVisible(false);
    }

    @FXML
    private void login(ActionEvent event) {
        val requestObject = this.collectRequestObject();
        try {
            val viewModel = this.controller.apply(requestObject);
            onSuccess(viewModel);
        } catch (LogUserInByCredentialsUseCase.UserNotFoundException |
                 LogUserInByCredentialsUseCase.PasswordMismatchException |
                 LogUserInByCredentialsUseCase.CredentialsInvalidException e) {
            MessageLabelFactory.createErrorLabel("Invalid username or password.", 14, errorMessage);
        }
    }

    private LogUserInByCredentialsController.@NonNull RequestObject collectRequestObject() {
        val username = usernameTextField.getText();
        val password = passwordTextField.getText();

        return LogUserInByCredentialsController.RequestObject.of(username, password.toCharArray());
    }

    private void onSuccess(LogUserInByCredentialsController.ViewModel viewModel) {
        val userType = viewModel.getUserType();

        if (userType.equals(LogUserInByCredentialsController.UserType.ADMINISTRATOR)) {
            StageManager.getInstance().loadAdminView(StageManager.ContentType.ADMIN_DASHBOARD);
        }
        else if (userType.equals(LogUserInByCredentialsController.UserType.STAFF)) {
            StageManager.getInstance().loadStaffView(StageManager.ContentType.STAFF_DASHBOARD);
        }
        else StageManager.getInstance().loadPatronView(StageManager.ContentType.PATRON_DASHBOARD);
    }

    @FXML
    private void register(ActionEvent event) {
        StageManager.getInstance().openSignupMenu();
    }
}

