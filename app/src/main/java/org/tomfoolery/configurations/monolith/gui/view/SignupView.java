package org.tomfoolery.configurations.monolith.gui.view;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.PatronRepository;
import org.tomfoolery.core.dataproviders.auth.PasswordService;
import org.tomfoolery.core.usecases.external.guest.auth.CreatePatronAccountUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.guest.auth.CreatePatronAccountController;

public class SignupView {
    private final @NonNull CreatePatronAccountController controller;

    public SignupView(
            @NonNull PatronRepository patronRepository,
            @NonNull PasswordService passwordService
    ) {
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
    private Button returnButton;

    @FXML
    private Label message;

    @FXML
    public void initialize() {
        message.setVisible(false);
        signupButton.setOnAction(this::signup);
        returnButton.setOnAction(this::returnToLogin);
    }

    private void signup(ActionEvent event) {
        try {
            val requestObject = getRequestObject();
            this.controller.accept(requestObject);

            message.setText("Registered Successfully, redirecting to login page...");
            message.setStyle("-fx-text-fill: #9dcc65");
            message.setVisible(true);

            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(this::returnToLogin);
            pause.play();
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
        message.setText("Password does not match.");
        message.setStyle("-fx-text-fill: #f7768e");
        message.setVisible(true);
    }

    private void onPatronCredentialsInvalidException() {
        System.out.println("Error: Provided credentials are invalid.");
        message.setText("Provided credentials are invalid.");
        message.setStyle("-fx-text-fill: #f7768e");
        message.setVisible(true);
    }

    private void onPatronAlreadyExistsException() {
        System.out.println("Error: Patron already exists.");
        message.setText("Patron already exists.");
        message.setStyle("-fx-text-fill: #f7768e");
        message.setVisible(true);
    }

    private static class PasswordMismatchException extends Exception {}

    private void returnToLogin(ActionEvent event) {
        StageManager.getInstance().openLoginMenu();
    }
}
