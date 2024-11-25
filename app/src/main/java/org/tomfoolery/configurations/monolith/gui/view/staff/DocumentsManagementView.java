package org.tomfoolery.configurations.monolith.gui.view.staff;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.user.LogOutView;
import org.tomfoolery.configurations.monolith.gui.view.user.ShowDocumentsView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;

import java.io.IOException;

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

    private void openDeleteDocumentDialog() {
    }

    private void openEditDocumentDialog() {
    }

    private void openAddDocumentMenu() {
        try {
            AddDocumentView controller = new AddDocumentView(
                    StageManager.getInstance().getDocumentRepository(),
                    StageManager.getInstance().getAuthenticationTokenGenerator(),
                    StageManager.getInstance().getAuthenticationTokenRepository(),
                    this
            );
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Staff/AddDocumentMenu.fxml"));
            loader.setController(controller);
            VBox v = loader.load();
            v.setMaxSize(800, 600);

            StackPane rootScene = StageManager.getInstance().getRootStackPane();
            rootScene.getChildren().add(v);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void updateDocument(ShowDocumentsView.DocumentViewModel documentViewModel) {}
}
