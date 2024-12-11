package org.tomfoolery.configurations.monolith.gui.view.patron.actions.auth;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.utils.BirthdayValidator;
import org.tomfoolery.configurations.monolith.gui.utils.MessageLabelFactory;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.usecases.external.patron.documents.borrow.retrieval.ShowBorrowedDocumentsUseCase;
import org.tomfoolery.core.usecases.external.patron.users.persistence.UpdatePatronMetadataUseCase;
import org.tomfoolery.core.usecases.external.patron.users.persistence.UpdatePatronPasswordUseCase;
import org.tomfoolery.core.usecases.external.patron.users.retrieval.GetPatronUsernameAndMetadataUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.patron.documents.borrow.retrieval.ShowBorrowedDocumentsController;
import org.tomfoolery.infrastructures.adapters.controllers.external.patron.users.persistence.UpdatePatronMetadataController;
import org.tomfoolery.infrastructures.adapters.controllers.external.patron.users.persistence.UpdatePatronPasswordController;
import org.tomfoolery.infrastructures.adapters.controllers.external.patron.users.retrieval.GetPatronUsernameAndMetadataController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;

public class PatronAccountCenter {
    private final @NonNull GetPatronUsernameAndMetadataController getUsernameAndMetadataController;
    private final @NonNull UpdatePatronMetadataController updateMetadataController;
    private final @NonNull UpdatePatronPasswordController updatePasswordController;
    private final @NonNull ShowBorrowedDocumentsController showBorrowedDocumentsController;

    public PatronAccountCenter(
            @NonNull PatronRepository patronRepository,
            @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
            @NonNull AuthenticationTokenRepository authenticationTokenRepository,
            @NonNull PasswordEncoder passwordEncoder,
            @NonNull DocumentRepository documentRepository,
            @NonNull BorrowingSessionRepository borrowingSessionRepository,
            @NonNull FileStorageProvider fileStorageProvider
            ) {
        this.getUsernameAndMetadataController = GetPatronUsernameAndMetadataController.of(
                patronRepository, authenticationTokenGenerator, authenticationTokenRepository
        );
        this.updateMetadataController = UpdatePatronMetadataController.of(
                patronRepository, authenticationTokenGenerator, authenticationTokenRepository
        );
        this.updatePasswordController = UpdatePatronPasswordController.of(
                patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder
        );
        this.showBorrowedDocumentsController = ShowBorrowedDocumentsController.of(
                documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository, fileStorageProvider
        );
    }

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private TextField birthdayTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField phoneNumberTextField;

    @FXML
    private TextField countryTextField;

    @FXML
    private TextField cityTextField;

    @FXML
    private Button saveProfileButton;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField oldPasswordTextField;

    @FXML
    private PasswordField newPasswordTextField;

    @FXML
    private Button saveSecurityButton;

    @FXML
    private Label dueDateLabel;

    @FXML
    private Label dueTitleLabel;

    @FXML
    private Label dueAuthorLabel;

    @FXML
    private ImageView dueImage;

    @FXML
    private Label borrowDateLabel;

    @FXML
    private Label borrowTitleLabel;

    @FXML
    private Label borrowAuthorLabel;

    @FXML
    private ImageView borrowImage;

    @FXML
    private Label saveMetadataMessage;

    @FXML
    private Label saveCredentialsMessage;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    public void initialize() {
        this.loadInitialInfo();
        this.loadDocumentsRelatedInfo();
        this.saveProfileButton.setOnAction(event -> saveMetadata());
        this.saveSecurityButton.setOnAction(event -> saveCredentials());
    }

    private void loadInitialInfo() {
        try {
            val viewModel = this.getUsernameAndMetadataController.get();
            String firstName = viewModel.getPatronFirstName();
            String lastName = viewModel.getPatronLastName();
            String birthday = viewModel.getPatronMonthOfBirth() + "/" + viewModel.getPatronDayOfBirth() + "/" + viewModel.getPatronYearOfBirth();
            String email = viewModel.getPatronEmail();
            String phoneNumber = viewModel.getPatronPhoneNumber();
            String country = viewModel.getPatronCountry();
            String city = viewModel.getPatronCity();
            String username = viewModel.getPatronUsername();

            firstNameTextField.setText(firstName);
            lastNameTextField.setText(lastName);
            birthdayTextField.setText(birthday);
            emailTextField.setText(email);
            phoneNumberTextField.setText(phoneNumber);
            countryTextField.setText(country);
            cityTextField.setText(city);
            usernameTextField.setText(username);
        } catch (GetPatronUsernameAndMetadataUseCase.AuthenticationTokenNotFoundException |
                 GetPatronUsernameAndMetadataUseCase.AuthenticationTokenInvalidException e) {
            StageManager.getInstance().openLoginMenu();
        } catch (GetPatronUsernameAndMetadataUseCase.PatronNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadDocumentsRelatedInfo() {
        val requestObject = ShowBorrowedDocumentsController.RequestObject.of(1, 1000);
        try {
            val viewModel = this.showBorrowedDocumentsController.apply(requestObject);
            val borrowedDocuments = viewModel.getPaginatedBorrowedDocuments();
            val latestBorrowedDocument = borrowedDocuments.getLast();
            val upcomingDueBorrowedDocument = borrowedDocuments.getFirst();

            dueTitleLabel.setText("- " + upcomingDueBorrowedDocument.getDocumentTitle());
            dueAuthorLabel.setText("- By: " + String.join(", ", upcomingDueBorrowedDocument.getDocumentAuthors()));
            dueImage.setImage(new Image("file:" + upcomingDueBorrowedDocument.getDocumentCoverImageFilePath(), 100, 150, false, true));

            borrowTitleLabel.setText("- " + latestBorrowedDocument.getDocumentTitle());
            borrowAuthorLabel.setText("- By: " + String.join(", ", latestBorrowedDocument.getDocumentAuthors()));
            borrowImage.setImage(new Image("file:" + latestBorrowedDocument.getDocumentCoverImageFilePath(), 100, 150, false, true));
        } catch (ShowBorrowedDocumentsUseCase.AuthenticationTokenInvalidException |
                 ShowBorrowedDocumentsUseCase.AuthenticationTokenNotFoundException e) {
            throw new RuntimeException(e);
        } catch (ShowBorrowedDocumentsUseCase.PaginationInvalidException e) {
            onException(e);
        }
    }

    public void saveMetadata() {
        try {
            val requestObject = this.getMetadataRequestObject();
            this.updateMetadataController.accept(requestObject);
            this.onSaveMetadataSuccess();

        } catch (UpdatePatronMetadataUseCase.AuthenticationTokenNotFoundException | UpdatePatronMetadataUseCase.AuthenticationTokenInvalidException exception) {
            StageManager.getInstance().openLoginMenu();
        } catch (BirthdayValidator.MonthOfBirthInvalidException |
                 BirthdayValidator.YearOfBirthInvalidException | BirthdayValidator.DayOfBirthInvalidException |
                 BirthdayValidator.BirthdayInvalidException exception) {
            this.onException(exception);
        } catch (UpdatePatronMetadataUseCase.PatronNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private UpdatePatronMetadataController.@NonNull RequestObject getMetadataRequestObject() throws BirthdayValidator.MonthOfBirthInvalidException, BirthdayValidator.YearOfBirthInvalidException, BirthdayValidator.DayOfBirthInvalidException, BirthdayValidator.BirthdayInvalidException {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String birthday = birthdayTextField.getText();
        String email = emailTextField.getText();
        String phoneNumber = phoneNumberTextField.getText();
        String country = countryTextField.getText();
        String city = cityTextField.getText();

        BirthdayValidator.validateBirthday(birthday);

        String[] birthdaySeparated = birthday.split("/");
        int monthOfBirth = Integer.parseInt(birthdaySeparated[0]);
        int dayOfBirth = Integer.parseInt(birthdaySeparated[1]);
        int yearOfBirth = Integer.parseInt(birthdaySeparated[2]);

        return UpdatePatronMetadataController.RequestObject.of(
                firstName, lastName, dayOfBirth, monthOfBirth, yearOfBirth, phoneNumber, city, country, email
        );
    }

    private void onSaveMetadataSuccess() {
        MessageLabelFactory.createSuccessLabel("New profile saved!", 16, saveMetadataMessage);
    }

    public void saveCredentials() {
        val requestObject = this.collectCredentialsRequestObject();
        System.out.println(scrollPane.getHeight() + " " + scrollPane.getWidth());

        try {
            this.updatePasswordController.accept(requestObject);
            this.onSaveCredentialsSuccess();

        } catch (UpdatePatronPasswordUseCase.AuthenticationTokenNotFoundException | AuthenticatedUserUseCase.AuthenticationTokenInvalidException exception) {
            StageManager.getInstance().openLoginMenu();
        } catch (UpdatePatronPasswordUseCase.PasswordInvalidException | UpdatePatronPasswordUseCase.PasswordMismatchException exception) {
            this.onException(exception);
        } catch (UpdatePatronPasswordUseCase.PatronNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private UpdatePatronPasswordController.@NonNull RequestObject collectCredentialsRequestObject() {
        char[] oldPassword = oldPasswordTextField.getText().toCharArray();
        char[] newPassword = newPasswordTextField.getText().toCharArray();

        return UpdatePatronPasswordController.RequestObject.of(oldPassword, newPassword);
    }

    private void onSaveCredentialsSuccess() {
        MessageLabelFactory.createSuccessLabel("New password saved!", 16, saveCredentialsMessage);
    }

    private void onException(Exception exception) {
        if (exception instanceof BirthdayValidator.DayOfBirthInvalidException) {
            MessageLabelFactory.createErrorLabel("Invalid day of birth!", 16, saveMetadataMessage);
        }
        if (exception instanceof BirthdayValidator.MonthOfBirthInvalidException) {
            MessageLabelFactory.createErrorLabel("Invalid month of birth!", 16, saveMetadataMessage);
        }
        if (exception instanceof BirthdayValidator.YearOfBirthInvalidException) {
            MessageLabelFactory.createErrorLabel("Invalid year of birth!", 16, saveMetadataMessage);
        }
        if (exception instanceof BirthdayValidator.BirthdayInvalidException) {
            MessageLabelFactory.createErrorLabel("Invalid birthday!", 16, saveMetadataMessage);
        }
        if (exception instanceof UpdatePatronPasswordUseCase.PasswordInvalidException) {
            MessageLabelFactory.createErrorLabel("Invalid password!", 16, saveCredentialsMessage);
        }
        if (exception instanceof UpdatePatronPasswordUseCase.PasswordMismatchException) {
            MessageLabelFactory.createErrorLabel("Password doesn't match!", 16, saveCredentialsMessage);
        }
        if (exception instanceof ShowBorrowedDocumentsUseCase.PaginationInvalidException) {
            borrowImage.setImage(null);
            borrowDateLabel.setText("None");
            borrowTitleLabel.setText("");
            borrowAuthorLabel.setText("");

            dueImage.setImage(null);
            dueDateLabel.setText("None");
            dueTitleLabel.setText("");
            dueAuthorLabel.setText("");
        }
    }
}
