package org.tomfoolery.configurations.monolith.gui.view.guest;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.usecases.guest.users.persistence.CreatePatronAccountUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.guest.users.persistence.CreatePatronAccountController;

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
    private TextField countryTextField;

    @FXML
    private TextField cityTextField;

    @FXML
    private TextField birthdayTextField;

    @FXML
    private TextField phoneNumberTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField retypePasswordField;

    @FXML
    private Button signupButton;

    @FXML
    private Label returnButton;

    @FXML
    private Label message;

    @FXML
    public void initialize() {
        message.setVisible(false);
        signupButton.setOnAction(this::signup);
        returnButton.setOnMouseClicked(event -> returnToLogin());
    }

    private void signup(ActionEvent event) {
        try {
            val requestObject = getRequestObject();
            this.controller.accept(requestObject);

            message.setText("Registered Successfully, redirecting to login page...");
            message.setStyle("-fx-text-fill: #9dcc65");
            message.setVisible(true);

            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(returnToLogin());
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
        String retypePassword = retypePasswordField.getText();

        String username = usernameTextField.getText();
        String password = passwordField.getText();
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();

        String birthday = birthdayTextField.getText();
        String[] birthdayComponents = birthday.split("/");
        int monthOfBirth = Integer.parseInt(birthdayComponents[0]);
        int dayOfBirth = Integer.parseInt(birthdayComponents[1]);
        int yearOfBirth = Integer.parseInt(birthdayComponents[2]);

        String phoneNumber = phoneNumberTextField.getText();
        String city = cityTextField.getText();
        String country = countryTextField.getText();
        String email = emailTextField.getText();

        char[] passwordCharArray = password.toCharArray();

        if (!password.equals(retypePassword))
            throw new PasswordMismatchException();

        return CreatePatronAccountController.RequestObject.of(
                username,
                passwordCharArray,
                firstName,
                lastName,
                dayOfBirth,
                monthOfBirth,
                yearOfBirth,
                phoneNumber,
                city,
                country,
                email
        );
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

    private EventHandler<ActionEvent> returnToLogin() {
        StageManager.getInstance().openLoginMenu();
        return null;
    }
}
