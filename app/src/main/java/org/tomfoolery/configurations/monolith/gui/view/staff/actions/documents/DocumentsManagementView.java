package org.tomfoolery.configurations.monolith.gui.view.staff.actions.documents;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.utils.ErrorDialog;
import org.tomfoolery.configurations.monolith.gui.view.user.documents.ShowDocumentsView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.common.documents.retrieval.ShowDocumentsUseCase;
import org.tomfoolery.core.usecases.external.common.documents.search.abc.SearchDocumentsUseCase;
import org.tomfoolery.core.usecases.external.staff.documents.persistence.UpdateDocumentContentUseCase;
import org.tomfoolery.core.usecases.external.staff.documents.persistence.UpdateDocumentCoverImageUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.common.documents.retrieval.ShowDocumentsController;
import org.tomfoolery.infrastructures.adapters.controllers.external.common.documents.search.SearchDocumentsController;
import org.tomfoolery.infrastructures.adapters.controllers.external.staff.documents.persistence.UpdateDocumentContentController;
import org.tomfoolery.infrastructures.adapters.controllers.external.staff.documents.persistence.UpdateDocumentCoverImageController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;

import java.io.File;
import java.util.function.Consumer;

public class DocumentsManagementView extends ShowDocumentsView{
    private final @NonNull UpdateDocumentCoverImageController updateDocumentCoverImageController = UpdateDocumentCoverImageController.of(
            StageManager.getInstance().getResources().getDocumentRepository(),
            StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
            StageManager.getInstance().getResources().getAuthenticationTokenRepository(),
            StageManager.getInstance().getResources().getFileVerifier(),
            StageManager.getInstance().getResources().getFileStorageProvider()
    );
    private final @NonNull UpdateDocumentContentController updateDocumentContentController = UpdateDocumentContentController.of(
            StageManager.getInstance().getResources().getDocumentRepository(),
            StageManager.getInstance().getResources().getDocumentContentRepository(),
            StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
            StageManager.getInstance().getResources().getAuthenticationTokenRepository(),
            StageManager.getInstance().getResources().getFileVerifier(),
            StageManager.getInstance().getResources().getFileStorageProvider()
    );

    @FXML
    private Button addDocumentButton;

    @FXML
    private Button showDocumentsWithoutContentButton;

    @FXML
    private TextField searchField;

//    @FXML
//    private ComboBox<Integer> pageChooserBox;

    @FXML
    private TableColumn<DocumentViewModel, Void> showDetailsColumn;

    @FXML
    private TableColumn<DocumentViewModel, Void> updateMetadataColumn;

    @FXML
    private TableColumn<DocumentViewModel, Void> updateCoverImageColumn;

    @FXML
    private TableColumn<DocumentViewModel, Void> updateContentColumn;

    @FXML
    private TableColumn<DocumentViewModel, Void> deleteDocumentColumn;

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
        addButtonToColumn(showDetailsColumn, "More details", this::showDocumentDetails);
        addButtonToColumn(updateMetadataColumn, "Update info", this::openUpdateDocumentMenu);
        addButtonToColumn(updateCoverImageColumn, "Update cover image", this::updateCoverImageMenu);
        addButtonToColumn(updateContentColumn, "Update PDF", this::updateContentMenu);
        addButtonToColumn(deleteDocumentColumn, "Delete", this::openDeleteDocumentDialog);

        addDocumentButton.setOnAction(event -> openAddDocumentMenu());
        showDocumentsWithoutContentButton.setOnAction(event -> StageManager.getInstance().loadStaffView(StageManager.ContentType.STAFF_SHOW_DOCUMENTS_WITHOUT_CONTENT));
        searchField.setOnAction(event -> this.searchDocuments());

        showDocuments();
    }

    private void searchDocuments() {
        final @NonNull SearchDocumentsController controller = SearchDocumentsController.of(
                StageManager.getInstance().getResources().getDocumentSearchGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository(),
                StageManager.getInstance().getResources().getFileStorageProvider()
        );

        if (searchField.getText().isEmpty()) {
            showDocuments();
            return;
        }

        val requestObject = SearchDocumentsController.RequestObject.of(SearchDocumentsController.SearchCriterion.TITLE, searchField.getText(), 1, Integer.MAX_VALUE);

        try {
            val viewModel = controller.apply(requestObject);
            documentsTable.getItems().clear();
            viewModel.getPaginatedDocuments().forEach(document -> documentsTable.getItems().add(new DocumentViewModel(document)));
        } catch (SearchDocumentsUseCase.AuthenticationTokenNotFoundException |
                 SearchDocumentsUseCase.AuthenticationTokenInvalidException e) {
            StageManager.getInstance().openLoginMenu();
        } catch (SearchDocumentsUseCase.PaginationInvalidException _) {
        }
    }

    private void updateContentMenu(@NonNull DocumentViewModel documentViewModel) {
        String contentFilePath = this.getPdfFilePath();
        val requestObject = UpdateDocumentContentController.RequestObject.of(documentViewModel.getDocumentISBN_13(), contentFilePath);
        try {
            this.updateDocumentContentController.accept(requestObject);
            showDocuments();
        } catch (UpdateDocumentContentController.DocumentContentFilePathInvalidException _) {
        } catch (UpdateDocumentContentUseCase.AuthenticationTokenNotFoundException |
                 UpdateDocumentContentUseCase.AuthenticationTokenInvalidException e) {
            StageManager.getInstance().openLoginMenu();
        } catch (UpdateDocumentContentUseCase.DocumentISBNInvalidException |
                 UpdateDocumentContentUseCase.DocumentNotFoundException e) {
            ErrorDialog.open("Document is invalid!");
        } catch (UpdateDocumentContentUseCase.DocumentContentInvalidException e) {
            ErrorDialog.open("Provided pdf file is invalid!");
        }
    }

    private @NonNull String getPdfFilePath() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF files", "*.pdf"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = StageManager.getInstance().getPrimaryStage();

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        }
        else return "";
    }

    private void updateCoverImageMenu(@NonNull DocumentViewModel documentViewModel) {
        String getCoverImagePath = this.openCoverImageFileChooser();
        val requestObject = UpdateDocumentCoverImageController.RequestObject.of(documentViewModel.getDocumentISBN_13(), getCoverImagePath);
        try {
            this.updateDocumentCoverImageController.accept(requestObject);
            showDocuments();
        } catch (UpdateDocumentCoverImageController.DocumentCoverImageFilePathInvalidException _) {
        } catch (UpdateDocumentCoverImageUseCase.AuthenticationTokenNotFoundException |
                 UpdateDocumentCoverImageUseCase.AuthenticationTokenInvalidException e) {
            StageManager.getInstance().openLoginMenu();
        } catch (UpdateDocumentCoverImageUseCase.DocumentISBNInvalidException |
                 UpdateDocumentCoverImageUseCase.DocumentNotFoundException e) {
            ErrorDialog.open("This document is invalid!");
        } catch (UpdateDocumentCoverImageUseCase.DocumentCoverImageInvalidException e) {
            ErrorDialog.open("The image provided is invalid.");
        }
    }

    public @NonNull String openCoverImageFileChooser() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG files", "*.png"),
                new FileChooser.ExtensionFilter("JPG files", "*.jpg"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = StageManager.getInstance().getPrimaryStage();

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        }
        else return "";
    }

    @SneakyThrows
    private void showDocumentDetails(DocumentViewModel document) {
        DocumentDetailsView controller = new DocumentDetailsView(document);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Staff/DocumentDetailsView.fxml"));
        loader.setController(controller);
        VBox v = loader.load();

        StageManager.getInstance().getRootStackPane().getChildren().add(v);
    }

    @Override
    public void showDocuments() {
        try {
            val requestObject = ShowDocumentsController.RequestObject.of(1, Integer.MAX_VALUE);
            val viewModel = this.controller.apply(requestObject);

            documentsTable.getItems().clear();
            viewModel.getPaginatedDocuments().forEach(document -> documentsTable.getItems().add(new DocumentViewModel(document)));
        } catch (ShowDocumentsUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (ShowDocumentsUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (ShowDocumentsUseCase.PaginationInvalidException exception) {
            this.onPaginationInvalidException();
        }
    }

    @SneakyThrows
    private void openDeleteDocumentDialog(@NonNull DocumentViewModel documentViewModel) {
        String selectedDocumentISBN = documentViewModel.getDocumentISBN_13();

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
    private void openUpdateDocumentMenu(@NonNull DocumentViewModel document) {
        UpdateDocumentMetadataView controller = new UpdateDocumentMetadataView(
                document,
                StageManager.getInstance().getResources().getDocumentRepository(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository()
        );

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Staff/UpdateDocumentMetadataView.fxml"));
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
}
