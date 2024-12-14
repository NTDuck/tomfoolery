package org.tomfoolery.configurations.monolith.gui.view.staff.actions.documents;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.utils.ErrorDialog;
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
    private ComboBox<Integer> pageChooser;

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

        this.setUpPageChooserBehaviour();
        this.showDocuments();
    }

    private void setUpPageChooserBehaviour() {
        pageChooser.getItems().clear();
        pageChooser.getItems().add(1);
        pageChooser.getSelectionModel().selectFirst();

        pageChooser.setOnAction(event -> showDocuments());
    }

    private void showDocuments() {
        int pageIndex = pageChooser.getSelectionModel().getSelectedItem();
        val requestObject = ShowDocumentsWithoutContentController.RequestObject.of(pageIndex, 60);
        try {
            val viewModel = this.showController.apply(requestObject);

            pageChooser.getItems().clear();
            for (int i = 1; i < viewModel.getMaxPageIndex(); ++i) {
                pageChooser.getItems().add(i);
            }
            EventHandler<ActionEvent> currentHandler = pageChooser.getOnAction();
            pageChooser.setOnAction(null);
            pageChooser.setValue(pageIndex);
            pageChooser.setOnAction(currentHandler);

            this.onSuccess(viewModel);
        } catch (ShowDocumentsWithoutContentUseCase.AuthenticationTokenInvalidException |
                 ShowDocumentsWithoutContentUseCase.AuthenticationTokenNotFoundException e) {
            StageManager.getInstance().openLoginMenu();
        } catch (ShowDocumentsWithoutContentUseCase.PaginationInvalidException e) {
            this.documentsTable.getItems().clear();
        }
    }

    private void onSuccess(ShowDocumentsWithoutContentController.@NonNull ViewModel viewModel) {
        documentsTable.getItems().clear();
        val documents = viewModel.getPaginatedDocumentsWithoutContent();

        Task<Void> loadDocumentsTask = new Task<>() {
            @Override
            protected Void call() {
                documents.forEach(document -> documentsTable.getItems().add(new DocumentViewModel(
                        document.getDocumentISBN_13(),
                        document.getDocumentTitle(),
                        String.join(", ", document.getDocumentAuthors()),
                        document.getCreatedTimestamp(),
                        document.getLastModifiedTimestamp()
                )));
                return null;
            }
        };

        loadDocumentsTask.setOnFailed(event -> {
            Throwable e = loadDocumentsTask.getException();
            System.err.println("Error loading documents: " + e.getMessage());
        });

        new Thread(loadDocumentsTask).start();
    }

    private void addContent(String isbn) {
        val content = this.getPdfFile();
        if (content == null) return;

        val requestObject = UpdateDocumentContentController.RequestObject.of(isbn, content.getAbsolutePath());

        try {
            this.updateController.accept(requestObject);
            showDocuments();
        } catch (UpdateDocumentContentController.DocumentContentFilePathInvalidException e) {
            ErrorDialog.open("Pdf path is invalid: " + content.getAbsolutePath());
        } catch (UpdateDocumentContentUseCase.AuthenticationTokenNotFoundException |
                 UpdateDocumentContentUseCase.AuthenticationTokenInvalidException e) {
            StageManager.getInstance().openLoginMenu();
        } catch (UpdateDocumentContentUseCase.DocumentISBNInvalidException |
                 UpdateDocumentContentUseCase.DocumentNotFoundException e) {
            ErrorDialog.open("This document's ISBN is invalid: ");
        } catch (UpdateDocumentContentUseCase.DocumentContentInvalidException e) {
            ErrorDialog.open("Pdf file is corrupted/invalid");
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
