package org.tomfoolery.configurations.monolith.gui.view.staff.actions.documents;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.user.documents.ShowDocumentsView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;

public class DocumentsManagementView extends ShowDocumentsView{
    @FXML
    private Button addDocumentButton;

    @FXML
    private Button editDocumentButton;

    @FXML
    private Button deleteDocumentButton;

    public DocumentsManagementView(
            @NonNull DocumentRepository documentRepository,
            @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
            @NonNull AuthenticationTokenRepository authenticationTokenRepository,
            @NonNull FileStorageProvider fileStorageProvider
            ) {
        super(documentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileStorageProvider);
    }

    @FXML @Override
    public void initialize() {
        addDocumentButton.setOnAction(event -> openAddDocumentMenu());
        editDocumentButton.setOnAction(event -> openUpdateDocumentMenu());
        deleteDocumentButton.setOnAction(event -> openDeleteDocumentDialog());

        showDocuments();
    }

    @SneakyThrows
    private void openDeleteDocumentDialog() {
        String selectedDocumentISBN = documentsTable.getSelectionModel().getSelectedItem().getISBN();

        DeleteDocumentPopup deleteDocumentPopupController = new DeleteDocumentPopup(
                selectedDocumentISBN,
                StageManager.getInstance().getResources().getDocumentRepository(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository()
        );

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Staff/DeleteDocumentPopup.fxml"));
        loader.setController(deleteDocumentPopupController);
        VBox v = loader.load();

        StageManager.getInstance().getRootStackPane().getChildren().add(v);
    }

    @SneakyThrows
    private void openUpdateDocumentMenu() {
        String ISBN = documentsTable.getSelectionModel().getSelectedItem().getISBN();
        String title = documentsTable.getSelectionModel().getSelectedItem().getTitle();
        String authors = documentsTable.getSelectionModel().getSelectedItem().getAuthors();
        String genres = documentsTable.getSelectionModel().getSelectedItem().getGenres();
        String description = documentsTable.getSelectionModel().getSelectedItem().getDescription();
        String publishedYear = documentsTable.getSelectionModel().getSelectedItem().getPublishedYear();
        String publisher = documentsTable.getSelectionModel().getSelectedItem().getPublisher();
        String created = documentsTable.getSelectionModel().getSelectedItem().getCreated();
        String lastModified = documentsTable.getSelectionModel().getSelectedItem().getLastModified();

        ShowDocumentsView.DocumentViewModel documentViewModel = DocumentViewModel.of(
                ISBN, title, authors, genres, description, publishedYear, publisher, created, lastModified
        );

        UpdateDocumentView controller = new UpdateDocumentView(
                documentViewModel,
                StageManager.getInstance().getResources().getDocumentRepository(),
                StageManager.getInstance().getResources().getDocumentContentRepository(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository(),
                StageManager.getInstance().getResources().getFileVerifier(),
                StageManager.getInstance().getResources().getFileStorageProvider()
        );

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Staff/UpdateDocumentView.fxml"));
        loader.setController(controller);
        VBox v = loader.load();

        StageManager.getInstance().getRootStackPane().getChildren().add(v);
    }

    @SneakyThrows
    private void openAddDocumentMenu() {
        AddDocumentView controller = new AddDocumentView(
                StageManager.getInstance().getResources().getDocumentRepository(),
                StageManager.getInstance().getResources().getDocumentContentRepository(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository(),
                StageManager.getInstance().getResources().getFileVerifier(),
                StageManager.getInstance().getResources().getFileStorageProvider()
        );
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Staff/AddDocumentView.fxml"));
        loader.setController(controller);
        VBox v = loader.load();

        StageManager.getInstance().getRootStackPane().getChildren().add(v);
    }
}
