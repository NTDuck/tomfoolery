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
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.utils.BirthdayValidator;
import org.tomfoolery.configurations.monolith.gui.utils.MessageLabelFactory;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.usecases.guest.users.persistence.CreatePatronAccountUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.guest.users.persistence.CreatePatronAccountController;

import java.util.Arrays;

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
        } catch(CreatePatronAccountUseCase.PatronCredentialsInvalidException exception) {
            this.onPatronCredentialsInvalidException();
        } catch (BirthdayValidator.BirthdayInvalidException exception) {
            this.onBirthdayInvalidException();
        } catch (BirthdayValidator.DayOfBirthInvalidException exception) {
            this.onDayOfBirthInvalidException();
        } catch (BirthdayValidator.MonthOfBirthInvalidException exception) {
            this.onMonthOfBirthInvalidException();
        } catch (BirthdayValidator.YearOfBirthInvalidException exception) {
            this.onYearOfBirthInvalidException();
        } catch (PasswordMismatchException exception) {
            this.onPasswordMismatchException();
        } catch (CreatePatronAccountUseCase.PatronAlreadyExistsException exception) {
            this.onPatronAlreadyExistsException();
        }
    }

    private CreatePatronAccountController.@NonNull RequestObject getRequestObject() throws BirthdayValidator.MonthOfBirthInvalidException, BirthdayValidator.YearOfBirthInvalidException, BirthdayValidator.DayOfBirthInvalidException, BirthdayValidator.BirthdayInvalidException {
        String username = usernameTextField.getText();
        char[] password = this.getPassword();
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();

        String[] birthday = this.getBirthday();
        int dayOfBirth = getDayOfBirth(birthday);
        int monthOfBirth = getMonthOfBirth(birthday);
        int yearOfBirth = getYearOfBirth(birthday);

        String phoneNumber = phoneNumberTextField.getText();
        String city = cityTextField.getText();
        String country = countryTextField.getText();
        String email = emailTextField.getText();

        return CreatePatronAccountController.RequestObject.of(
                username,
                password,
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

    private char @NonNull [] getPassword() throws PasswordMismatchException {
        val password = this.passwordField.getText().toCharArray();
        val repeatPassword = this.retypePasswordField.getText().toCharArray();

        if (!Arrays.equals(password, repeatPassword))
            throw new PasswordMismatchException();

        return password;
    }

    private @NonNull String[] getBirthday() {
        String birthday = this.birthdayTextField.getText();

        try {
            BirthdayValidator.validateBirthday(birthday);
        } catch (BirthdayValidator.BirthdayInvalidException e) {
            this.onBirthdayInvalidException();
        } catch (BirthdayValidator.MonthOfBirthInvalidException e) {
            this.onMonthOfBirthInvalidException();
        } catch (BirthdayValidator.DayOfBirthInvalidException e) {
            this.onDayOfBirthInvalidException();
        } catch (BirthdayValidator.YearOfBirthInvalidException e) {
            this.onYearOfBirthInvalidException();
        }

        return birthday.split("/");
    }

    private @Unsigned int getDayOfBirth(String[] birthday) {
        return Integer.parseInt(birthday[1]);
    }

    private @Unsigned int getMonthOfBirth(String[] birthday) {
        return Integer.parseInt(birthday[0]);
    }

    private @Unsigned int getYearOfBirth(String[] birthday) {
        return Integer.parseInt(birthday[2]);
    }

    private void onPatronCredentialsInvalidException() {
        MessageLabelFactory.createErrorLabel("Username or password is not safe enough.", 14, message);
    }

    private void onPasswordMismatchException() {
        MessageLabelFactory.createErrorLabel("Password does not match.", 14, message);
    }

    private void onBirthdayInvalidException() {
        MessageLabelFactory.createErrorLabel("Provided birthday is invalid.", 14, message);
    }

    private void onDayOfBirthInvalidException() {
        MessageLabelFactory.createErrorLabel("Day of birth is invalid.", 14, message);

    }

    private void onMonthOfBirthInvalidException() {
        MessageLabelFactory.createErrorLabel("Month of birth is invalid.", 14, message);
    }

    private void onYearOfBirthInvalidException() {
        MessageLabelFactory.createErrorLabel("Year of birth is invalid.", 14, message);
    }

    private void onPatronAlreadyExistsException() {
        MessageLabelFactory.createErrorLabel("Patron already exists.", 14, message);
    }

    private EventHandler<ActionEvent> returnToLogin() {
        StageManager.getInstance().openLoginMenu();
        return null;
    }

    private static class PasswordMismatchException extends RuntimeException {}
}
