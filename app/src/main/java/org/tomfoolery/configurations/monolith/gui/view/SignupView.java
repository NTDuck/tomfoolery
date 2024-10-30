package org.tomfoolery.configurations.monolith.gui.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.PatronRepository;
import org.tomfoolery.core.dataproviders.auth.PasswordService;
import org.tomfoolery.core.usecases.external.guest.auth.CreatePatronAccountUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.guest.auth.CreatePatronAccountController;

public class SignupView {
    private final @NonNull CreatePatronAccountController controller;

    public SignupView(@NonNull PatronRepository patronRepository, @NonNull PasswordService passwordService) {
        controller = CreatePatronAccountController.of(patronRepository, passwordService);
    }

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField addressTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private PasswordField retypePasswordTextField;

    @FXML
    private Button signupButton;

    @FXML
    private Button returnToLoginButton;

    @FXML
    public void initialize() {
        signupButton.setOnAction(this::signup);
        returnToLoginButton.setOnAction(this::returnToLogin);
    }

    private void signup(ActionEvent event) {
        try {
            val requestObject = getRequestObject();
            this.controller.accept(requestObject);
            System.out.println("registered successfully");
        } catch (PasswordMismatchException exception) {
            onPasswordMismatchException();
        } catch (CreatePatronAccountUseCase.PatronCredentialsInvalidException e) {
            onPatronCredentialsInvalidException();
        } catch (CreatePatronAccountUseCase.PatronAlreadyExistsException e) {
            onPatronAlreadyExistsException();
        }
    }

    private CreatePatronAccountController.@NonNull RequestObject getRequestObject() throws PasswordMismatchException {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        String retypePassword = retypePasswordTextField.getText();
        String address = addressTextField.getText();

        if (!password.equals(retypePassword))
            throw new PasswordMismatchException();

        return CreatePatronAccountController.RequestObject.of(username, password, firstName, lastName, address, "");
    }

    private void onPasswordMismatchException() {
        System.out.println("Error: Password does not match.");
    }

    private void onPatronCredentialsInvalidException() {
        System.out.println("Error: Provided credentials are invalid.");
    }

    private void onPatronAlreadyExistsException() {
        System.out.println("Error: Patron already exists.");
    }

    private static class PasswordMismatchException extends Exception {}

    @FXML
    private void returnToLogin(ActionEvent event) {
        StageManager.openLoginMenu();
    }
}
