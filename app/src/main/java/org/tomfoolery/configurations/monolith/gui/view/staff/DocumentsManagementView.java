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
        documentsTable.setRowFactory(tv -> {
            TableRow<ShowDocumentsView.DocumentViewModel> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem updateItem = new MenuItem("Update Content");
            updateItem.setOnAction(e -> {
                ShowDocumentsView.DocumentViewModel selectedItem = row.getItem();
                if (selectedItem != null) {
                    updateDocument(selectedItem);
                }
            });

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(e -> {
                ShowDocumentsView.DocumentViewModel selectedItem = row.getItem();
                if (selectedItem != null) {
                    documentsTable.getItems().remove(selectedItem);
                }
            });

            contextMenu.getItems().addAll(updateItem, deleteItem);

            row.setContextMenu(contextMenu);

            return row;
        });
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
                    StageManager.getInstance().getAuthenticationTokenRepository()
            );
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Staff/AddDocumentMenu.fxml"));
            loader.setController(controller);
            VBox v = loader.load();

            StackPane rootScene = (StackPane) StageManager.getInstance().getPrimaryStage().getScene().getRoot();
            rootScene.getChildren().add(v);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void updateDocument(ShowDocumentsView.DocumentViewModel documentViewModel) {
//        TextInputDialog dialog = new TextInputDialog(documentViewModel.getTitle());
//        dialog.setTitle("Update Document");
//        dialog.setHeaderText("Update Title");
//        dialog.setContentText("Enter new title:");
//
//        dialog.showAndWait().ifPresent(newTitle -> {
//            // Update the title of the document
//            documentViewModel.getDocument().getMetadata().setTitle(newTitle);
//
//            // Refresh the table view to reflect changes
//            documentsTable.refresh();
//        });
    }
//
//    @AllArgsConstructor
//    @Getter
//    public static class DocumentViewModel {
//        final Document document;
//        final int index;
//        final String ISBN;
//        final String title;
//        final String authors;
//        final String genres;
//        final String description;
//        final String publishedYear;
//        final String created;
//        final String lastModified;
//
//        public DocumentViewModel(Document document, int index) {
//            this.index = index;
//            this.document = document;
//            ISBN = document.getId().getISBN();
//            title = document.getMetadata().getTitle();
//            authors = String.join(", ", document.getMetadata().getAuthors());
//            genres = String.join(", ", document.getMetadata().getGenres());
//            description = document.getMetadata().getDescription();
//            publishedYear = document.getMetadata().getPublishedYear().toString();
//            created = String.valueOf(document.getAudit().getTimestamps().getCreated());
//            lastModified = String.valueOf(document.getAudit().getTimestamps().getLastModified());
//
//        }
//    }
}
