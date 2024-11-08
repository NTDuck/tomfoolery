package org.tomfoolery.configurations.monolith.gui.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.val;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.dataproviders.auth.PasswordService;
import org.tomfoolery.core.domain.Administrator;
import org.tomfoolery.core.domain.Staff;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.adapters.controllers.guest.auth.LogUserInController;
import org.tomfoolery.infrastructures.adapters.presenters.guest.auth.LogUserInPresenter;
import org.checkerframework.checker.nullness.qual.NonNull;

public class LoginView {
    private final @NonNull LogUserInController controller;
    private final @NonNull LogUserInPresenter presenter;
    private final @NonNull StageManager stageManager;

    public LoginView(@NonNull UserRepositories userRepositories, @NonNull PasswordService passwordService, 
                     @NonNull AuthenticationTokenService authenticationTokenService,
                     @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull StageManager stageManager) {
        this.controller = LogUserInController.of(userRepositories, passwordService, authenticationTokenService, authenticationTokenRepository);
        this.presenter = LogUserInPresenter.of(authenticationTokenService);
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
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        val requestObject = LogUserInController.RequestObject.of(username, password);

        try {
            val responseModel = this.controller.apply(requestObject);
            val viewModel = this.presenter.apply(responseModel);
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

    private @NonNull String getFXMLPathFromViewModel(LogUserInPresenter.@NonNull ViewModel viewModel) {
        val userClass = viewModel.getUserClass();

        if (userClass.equals(Administrator.class)) {
            return "/fxml/Admin/Dashboard.fxml";
        } else if (userClass.equals(Staff.class)) {
            return "/fxml/Staff/Dashboard.fxml";
        } else return "/fxml/Patron/Dashboard.fxml";
    }
}
