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
            @NonNull AuthenticationTokenRepository authenticationTokenRepository
    ) {
        super(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @FXML @Override
    public void initialize() {
        addDocumentButton.setOnAction(event -> openAddDocumentMenu());
        editDocumentButton.setOnAction(event -> openEditDocumentDialog());
        deleteDocumentButton.setOnAction(event -> openDeleteDocumentDialog());

        showDocuments();
    }

    @SneakyThrows
    private void openDeleteDocumentDialog() {
        String selectedDocumentISBN = documentsTable.getSelectionModel().getSelectedItem().getISBN();

        DeleteDocumentPopup deleteDocumentPopupController = new DeleteDocumentPopup(
                selectedDocumentISBN,
                StageManager.getInstance().getDocumentRepository(),
                StageManager.getInstance().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getAuthenticationTokenRepository()
        );

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Staff/DeleteDocumentPopup.fxml"));
        loader.setController(deleteDocumentPopupController);
        VBox v = loader.load();

        StageManager.getInstance().getRootStackPane().getChildren().add(v);
    }

    private void openEditDocumentDialog() {

    }

    @SneakyThrows
    private void openAddDocumentMenu() {
        AddDocumentView controller = new AddDocumentView(
                StageManager.getInstance().getDocumentRepository(),
                StageManager.getInstance().getDocumentContentRepository(),
                StageManager.getInstance().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getAuthenticationTokenRepository(),
                this
        );
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Staff/AddDocumentView.fxml"));
        loader.setController(controller);
        VBox v = loader.load();

        StageManager.getInstance().getRootStackPane().getChildren().add(v);
    }

    private void updateDocument(ShowDocumentsView.DocumentViewModel documentViewModel) {}
}
