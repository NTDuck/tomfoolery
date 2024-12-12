package org.tomfoolery.configurations.monolith.gui.view.staff.actions.documents;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.user.documents.ShowDocumentsView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.providers.io.file.FileVerifier;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.usecases.external.staff.documents.persistence.UpdateDocumentContentUseCase;
import org.tomfoolery.core.usecases.external.staff.documents.persistence.UpdateDocumentCoverImageUseCase;
import org.tomfoolery.core.usecases.external.staff.documents.persistence.UpdateDocumentMetadataUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.staff.documents.persistence.UpdateDocumentContentController;
import org.tomfoolery.infrastructures.adapters.controllers.external.staff.documents.persistence.UpdateDocumentCoverImageController;
import org.tomfoolery.infrastructures.adapters.controllers.external.staff.documents.persistence.UpdateDocumentMetadataController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;

public class UpdateDocumentMetadataView {
    private final @NonNull UpdateDocumentMetadataController metadataController;

    private final ShowDocumentsView.@NonNull DocumentViewModel documentViewModel;

    @FXML
    private TextField title;

    @FXML
    private TextField ISBN;

    @FXML
    private TextField authors;

    @FXML
    private TextField genres;

    @FXML
    private TextArea description;

    @FXML
    private TextField publisher;

    @FXML
    private TextField publishedYear;

    @FXML
    private Label errorMessage;

    @FXML
    private Button updateDocumentButton;

    @FXML
    private Button cancelButton;

    public UpdateDocumentMetadataView(
            ShowDocumentsView.@NonNull DocumentViewModel documentViewModel,
            @NonNull DocumentRepository documentRepository,
            @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
            @NonNull AuthenticationTokenRepository authenticationTokenRepository
    ) {
        this.documentViewModel = documentViewModel;
        this.metadataController = UpdateDocumentMetadataController.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @FXML
    public void initialize() {
        ISBN.setEditable(false);
        errorMessage.setVisible(false);
        loadInitialInfo();
        updateDocumentButton.setOnAction(event -> updateDocument());
        cancelButton.setOnAction(event -> closeView());
    }

    private void loadInitialInfo() {
        ISBN.setText(documentViewModel.getDocumentISBN_13());
        title.setText(documentViewModel.getDocumentTitle());
        authors.setText(documentViewModel.getDocumentAuthors());
        genres.setText(documentViewModel.getDocumentGenres());
        description.setText(documentViewModel.getDocumentDescription());
        publisher.setText(documentViewModel.getDocumentPublisher());
        publishedYear.setText(documentViewModel.getDocumentPublishedYear());
    }

    public void closeView() {
        StageManager.getInstance().getRootStackPane().getChildren().removeLast();
    }

    public void updateDocument() {
        try {
            val metadataRequestObject = this.collectMetadataRequestObject();
            this.metadataController.accept(metadataRequestObject);

            this.onSuccess();
        } catch (DocumentPublishedYearInvalidException |
                 UpdateDocumentMetadataController.DocumentPublishedYearInvalidException e) {
            onDocumentPublishedYearInvalidException();
        } catch (UpdateDocumentMetadataUseCase.AuthenticationTokenInvalidException |
                 UpdateDocumentMetadataUseCase.AuthenticationTokenNotFoundException e) {
            StageManager.getInstance().openLoginMenu();
        } catch (UpdateDocumentMetadataUseCase.DocumentNotFoundException |
                 UpdateDocumentMetadataUseCase.DocumentISBNInvalidException e) {
            onDocumentNotFoundException();
        }
    }

    private void onDocumentNotFoundException() {
        showErrorMessage("Document not found or ISBN is invalid!");
    }

    private UpdateDocumentMetadataController.@NonNull RequestObject collectMetadataRequestObject() throws DocumentPublishedYearInvalidException {
        String ISBN = this.ISBN.getText();

        String documentTitle = title.getText();
        String documentDescription = description.getText();
        String rawDocumentAuthors = authors.getText();
        String rawDocumentGenres = genres.getText();

        short documentPublishedYear = this.collectDocumentPublishedYear();
        String documentPublisher = publisher.getText();

        val documentAuthors = Arrays.asList(Arrays.stream(rawDocumentAuthors.split(",")).parallel().map(String::trim).toArray(String[]::new));
        val documentGenres = Arrays.asList(Arrays.stream(rawDocumentGenres.split(",")).parallel().map(String::trim).toArray(String[]::new));

        return UpdateDocumentMetadataController.RequestObject.of(ISBN, documentTitle, documentDescription, documentAuthors, documentGenres, documentPublishedYear, documentPublisher);
    }

    private @Unsigned short collectDocumentPublishedYear() throws DocumentPublishedYearInvalidException {
        val rawDocumentPublishedYear = publishedYear.getText();

        try {
            return Short.parseShort(rawDocumentPublishedYear);
        } catch (NumberFormatException exception) {
            throw new DocumentPublishedYearInvalidException();
        }
    }

    private void onSuccess() {
        errorMessage.setVisible(false);
        closeView();
        StageManager.getInstance().loadStaffView(StageManager.ContentType.STAFF_DOCUMENTS_MANAGEMENT);
    }

    private void showErrorMessage(String message) {
        errorMessage.setVisible(true);
        errorMessage.setText(message);
    }

    private void onDocumentPublishedYearInvalidException() {
        showErrorMessage("Document published year invalid");
    }

    private static class DocumentPublishedYearInvalidException extends Exception {}
}
