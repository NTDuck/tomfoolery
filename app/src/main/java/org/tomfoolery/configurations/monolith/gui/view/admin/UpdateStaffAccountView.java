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
import org.tomfoolery.core.usecases.administrator.users.persistence.UpdateStaffCredentialsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.administrator.users.persistence.UpdateStaffCredentialsController;
import org.tomfoolery.infrastructures.utils.helpers.adapters.UserIdBiAdapter;

public class UpdateStaffAccountView {
    private final @NonNull UpdateStaffCredentialsController controller;
    private final @NonNull StaffAccountsManagementView parentView;
    private final @NonNull String staffId;

    public UpdateStaffAccountView(
            @NonNull StaffAccountsManagementView parentView,
            @NonNull String staffId,
            @NonNull StaffRepository staffRepository,
            @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
            @NonNull AuthenticationTokenRepository authenticationTokenRepository,
            @NonNull PasswordEncoder passwordEncoder
    ) {
        this.staffId = staffId;
        this.parentView = parentView;
        this.controller = UpdateStaffCredentialsController.of(staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
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
        usernameTextField.setOnAction(event -> updateStaffAccount());
        passwordTextField.setOnAction(event -> updateStaffAccount());
        cancelButton.setOnAction(event -> closeView());
        confirmButton.setOnAction(event -> updateStaffAccount());
    }

    private void updateStaffAccount() {
        val requestObject = UpdateStaffCredentialsController.RequestObject.of(
                staffId, usernameTextField.getText(), passwordTextField.getText().toCharArray()
        );

        try {
            this.controller.accept(requestObject);
            this.parentView.loadAccounts();
            closeView();
        } catch (UpdateStaffCredentialsUseCase.AuthenticationTokenNotFoundException |
                 UpdateStaffCredentialsUseCase.AuthenticationTokenInvalidException e) {
            System.err.println("You are not an admin, or you simply don't exist");
        } catch (UpdateStaffCredentialsUseCase.StaffCredentialsInvalidException e) {
            errorMessage.setText("Try a safer password");
        } catch (UpdateStaffCredentialsUseCase.StaffNotFoundException e) {
            System.err.println("This never happens btw");
        } catch (UserIdBiAdapter.UserUuidInvalidException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeView() {
        StageManager.getInstance().getRootStackPane().getChildren().removeLast();
    }
}
