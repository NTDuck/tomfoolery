package org.tomfoolery.configurations.monolith.gui.view.staff.actions.documents;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.providers.io.file.FileVerifier;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.staff.documents.persistence.UpdateDocumentContentUseCase;
import org.tomfoolery.core.usecases.external.staff.documents.retrieval.ShowDocumentsWithoutContentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.staff.documents.persistence.UpdateDocumentContentController;
import org.tomfoolery.infrastructures.adapters.controllers.external.staff.documents.retrieval.ShowDocumentsWithoutContentController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;
import java.io.File;
import java.util.function.Consumer;

public class UpdateDocumentContentView {
    private final @NonNull UpdateDocumentContentController updateController;
    private final @NonNull ShowDocumentsWithoutContentController showController;

    public UpdateDocumentContentView(
            @NonNull DocumentRepository documentRepository,
            @NonNull DocumentContentRepository documentContentRepository,
            @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
            @NonNull AuthenticationTokenRepository authenticationTokenRepository,
            @NonNull FileStorageProvider fileStorageProvider,
            @NonNull FileVerifier fileVerifier
            ) {
        this.updateController = UpdateDocumentContentController.of(
                documentRepository,
                documentContentRepository,
                authenticationTokenGenerator,
                authenticationTokenRepository,
                fileVerifier,
                fileStorageProvider
        );
        this.showController = ShowDocumentsWithoutContentController.of(
                documentRepository,
                documentContentRepository,
                authenticationTokenGenerator,
                authenticationTokenRepository,
                fileStorageProvider
        );
    }

    @FXML
    private TableView<DocumentViewModel> documentsTable;

    @FXML
    private TableColumn<DocumentViewModel, Void> addContentColumn;

    @FXML
    private Label headerLabel;

    @FXML
    public void initialize() {
        addButtonToColumn(addContentColumn, "Add content", (DocumentViewModel document) -> {
            addContent(document.getIsbn());
        });

        this.showDocuments();
    }

    private void showDocuments() {
        val requestObject = ShowDocumentsWithoutContentController.RequestObject.of(1, Integer.MAX_VALUE);
        try {
            val viewModel = this.showController.apply(requestObject);

            documentsTable.getItems().clear();
            viewModel.getPaginatedDocumentsWithoutContent().forEach(document -> {
                    documentsTable.getItems().add(new DocumentViewModel(
                            document.getDocumentISBN_13(),
                            document.getDocumentTitle(),
                            String.join(", ", document.getDocumentAuthors()),
                            document.getCreatedTimestamp(),
                            document.getLastModifiedTimestamp()
                    ));
            });

        } catch (ShowDocumentsWithoutContentUseCase.AuthenticationTokenInvalidException |
                 ShowDocumentsWithoutContentUseCase.AuthenticationTokenNotFoundException e) {
            StageManager.getInstance().openLoginMenu();
        } catch (ShowDocumentsWithoutContentUseCase.PaginationInvalidException _) {
        }
    }

    private void addContent(String isbn) {
        val content = this.getPdfFile();
        if (content == null) return;

        val requestObject = UpdateDocumentContentController.RequestObject.of(isbn, content.getAbsolutePath());

        try {
            this.updateController.accept(requestObject);
            showDocuments();
        } catch (UpdateDocumentContentController.DocumentContentFilePathInvalidException e) {
            System.err.println("Pdf path is invalid: " + content.getAbsolutePath());
        } catch (UpdateDocumentContentUseCase.AuthenticationTokenNotFoundException |
                 UpdateDocumentContentUseCase.AuthenticationTokenInvalidException e) {
            StageManager.getInstance().openLoginMenu();
        } catch (UpdateDocumentContentUseCase.DocumentISBNInvalidException |
                 UpdateDocumentContentUseCase.DocumentNotFoundException e) {
            System.err.println("This document's ISBN is invalid: ");
        } catch (UpdateDocumentContentUseCase.DocumentContentInvalidException e) {
            System.err.println("Pdf file is corrupted/invalid");
        }
    }

    private void addButtonToColumn(@NonNull TableColumn<DocumentViewModel, Void> column, String buttonText, Consumer<DocumentViewModel> action) {
        column.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button(buttonText);

            {
                button.setOnAction(event -> {
                    DocumentViewModel document = getTableView().getItems().get(getIndex());
                    action.accept(document);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                }
            }
        });
    }

    private File getPdfFile() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF files", "*.pdf"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = StageManager.getInstance().getPrimaryStage();

        return fileChooser.showOpenDialog(stage);
    }

    @Value
    public static class DocumentViewModel {
        String isbn;
        String title;
        String authors;
        String created;
        String lastModified;
    }
}
