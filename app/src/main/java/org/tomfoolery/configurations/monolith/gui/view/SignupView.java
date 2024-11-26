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
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.usecases.guest.auth.CreatePatronAccountUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.guest.auth.CreatePatronAccountController;

public class SignupView {
    private final @NonNull CreatePatronAccountController controller;

    public SignupView(
            @NonNull PatronRepository patronRepository,
            @NonNull PasswordEncoder passwordService
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
        firstNameTextField.setOnAction(this::signup);
        lastNameTextField.setOnAction(this::signup);
        usernameTextField.setOnAction(this::signup);
        addressTextField.setOnAction(this::signup);
        emailTextField.setOnAction(this::signup);
        passwordTextField.setOnAction(this::signup);
        retypePasswordTextField.setOnAction(this::signup);
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
        } catch (CreatePatronAccountUseCase.PatronCredentialsInvalidException exception) {
            this.onPatronCredentialsInvalidException();
        } catch (CreatePatronAccountUseCase.PatronAlreadyExistsException exception) {
            this.onPatronAlreadyExistsException();
        } catch (PasswordMismatchException exception) {
            this.onPasswordMismatchException();
        }
    }

    private CreatePatronAccountController.@NonNull RequestObject getRequestObject() throws PasswordMismatchException {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        String retypePassword = retypePasswordTextField.getText();
        String address = addressTextField.getText();
        char[] passwordCharArray = password.toCharArray();

        if (!password.equals(retypePassword))
            throw new PasswordMismatchException();

        return CreatePatronAccountController.RequestObject.of(username, passwordCharArray, firstName, lastName, address, "");
    }

    private void onPasswordMismatchException() {
        message.setText("Password does not match.");
        message.setStyle("-fx-text-fill: #f7768e");
        message.setVisible(true);
    }

    private void onPatronCredentialsInvalidException() {
        message.setText("Provided credentials are invalid.");
        message.setStyle("-fx-text-fill: #f7768e");
        message.setVisible(true);
    }

    private void onPatronAlreadyExistsException() {
        message.setText("Patron already exists.");
        message.setStyle("-fx-text-fill: #f7768e");
        message.setVisible(true);
    }

    private static class PasswordMismatchException extends Exception {}

    private void returnToLogin(ActionEvent event) {
        StageManager.getInstance().openLoginMenu();
    }
}
