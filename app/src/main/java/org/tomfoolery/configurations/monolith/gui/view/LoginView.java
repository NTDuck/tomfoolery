package org.tomfoolery.configurations.monolith.gui.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.val;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.domain.auth.Administrator;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.adapters.controllers.guest.auth.LogUserInController;
import org.checkerframework.checker.nullness.qual.NonNull;

public class LoginView {
    private final @NonNull LogUserInController controller;
    private final @NonNull StageManager stageManager;

    public LoginView(@NonNull UserRepositories userRepositories, @NonNull PasswordEncoder passwordEncoder, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
                     @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull StageManager stageManager) {
        this.controller = LogUserInController.of(userRepositories, passwordEncoder, authenticationTokenGenerator, authenticationTokenRepository);
        this.stageManager = stageManager;
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
        val requestObject = collectRequestObject();

        try {
            val viewModel = this.controller.apply(requestObject);

            stageManager.openMenu(getFXMLPathFromViewModel(viewModel), "Dashboard");
        } catch (Exception exception) {
            errorMessage.setText("Invalid username or password");
            errorMessage.setVisible(true);
        }
    }

    @FXML
    private void register(ActionEvent event) {
        stageManager.openSignupMenu();
    }

    private LogUserInController.@NonNull RequestObject collectRequestObject() {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        return LogUserInController.RequestObject.of(username, password);
    }

    private @NonNull String getFXMLPathFromViewModel(LogUserInController.@NonNull ViewModel viewModel) {
        val userClass = viewModel.getUserClass();

        if (userClass.equals(Administrator.class)) {
            return "/fxml/Admin/Dashboard.fxml";
        } else if (userClass.equals(Staff.class)) {
            return "/fxml/Staff/Dashboard.fxml";
        } else return "/fxml/Patron/Dashboard.fxml";
    }
}
