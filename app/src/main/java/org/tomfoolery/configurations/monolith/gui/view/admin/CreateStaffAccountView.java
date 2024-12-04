package org.tomfoolery.configurations.monolith.gui.view.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.administrator.users.persistence.CreateStaffAccountUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.administrator.users.persistence.CreateStaffAccountController;

public class CreateStaffAccountView {
    private final @NonNull CreateStaffAccountController controller;
    private final @NonNull StaffAccountsManagementView parentView;

    public CreateStaffAccountView(
            @NonNull StaffAccountsManagementView parentView,
            @NonNull StaffRepository staffRepository,
            @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
            @NonNull AuthenticationTokenRepository authenticationTokenRepository,
            @NonNull PasswordEncoder passwordEncoder
            ) {
        this.parentView = parentView;
        this.controller = CreateStaffAccountController.of(staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmButton;

    @FXML
    private Label errorMessage;

    @FXML
    public void initialize() {
        usernameTextField.setOnAction(event -> createStaffAccount());
        passwordTextField.setOnAction(event -> createStaffAccount());
        cancelButton.setOnAction(event -> closeView());
        confirmButton.setOnAction(event -> createStaffAccount());
    }

    private void createStaffAccount() {
        val requestObject = CreateStaffAccountController.RequestObject.of(
                usernameTextField.getText(), passwordTextField.getText().toCharArray()
        );

        try {
            this.controller.accept(requestObject);
            this.parentView.loadAccounts();
            closeView();
        } catch (CreateStaffAccountUseCase.AuthenticationTokenNotFoundException |
                 CreateStaffAccountUseCase.AuthenticationTokenInvalidException e) {
            System.err.println("You are not an admin, or you simply don't exist");
        } catch (CreateStaffAccountUseCase.StaffCredentialsInvalidException e) {
            errorMessage.setText("Try a safer password...");
            errorMessage.setVisible(true);
        } catch (CreateStaffAccountUseCase.StaffAlreadyExistsException e) {
            errorMessage.setText("This staff already exists");
            errorMessage.setVisible(true);
        }
    }

    private void closeView() {
        StageManager.getInstance().getRootStackPane().getChildren().removeLast();
    }
}
