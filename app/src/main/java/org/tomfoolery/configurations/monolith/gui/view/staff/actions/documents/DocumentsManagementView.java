package org.tomfoolery.configurations.monolith.gui.view.staff.actions.documents;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.user.documents.ShowDocumentsView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.common.documents.retrieval.ShowDocumentsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.common.documents.retrieval.ShowDocumentsController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;

import java.util.function.Consumer;

public class DocumentsManagementView extends ShowDocumentsView{
    @FXML
    private Button addDocumentButton;

    @FXML
    private Button showDocumentsWithoutContentButton;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<Integer> pageChooserBox;

    @FXML
    private TableColumn<DocumentViewModel, Void> showDetailsColumn;

    @FXML
    private TableColumn<DocumentViewModel, Void> updateMetadataColumn;

    @FXML
    private TableColumn<DocumentViewModel, Void> updateCoverImageColumn;

    @FXML
    private TableColumn<DocumentViewModel, Void> updateContentColumn;

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
        addButtonToColumn(updateCoverImageColumn, "Update cover image", document -> );
        addButtonToColumn(updateContentColumn, "Update PDF", document -> );

        addDocumentButton.setOnAction(event -> openAddDocumentMenu());


        showDocuments();
    }

    private void showDocumentDetails(DocumentViewModel document) {

    }

    @Override
    public void showDocuments() {
        try {
            val requestObject = ShowDocumentsController.RequestObject.of(1, 30);
            val viewModel = this.controller.apply(requestObject);

            documentsTable.getItems().clear();
            viewModel.getPaginatedDocuments().forEach(document -> {
                documentsTable.getItems().add(new DocumentViewModel(document));
            });

        } catch (ShowDocumentsUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (ShowDocumentsUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (ShowDocumentsUseCase.PaginationInvalidException exception) {
            this.onPaginationInvalidException();
        }
    }

    @SneakyThrows
    private void openDeleteDocumentDialog() {
        String selectedDocumentISBN = documentsTable.getSelectionModel().getSelectedItem().getDocumentISBN_13();

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
    private void openUpdateDocumentMenu(DocumentViewModel document) {
        UpdateDocumentMetadataView controller = new UpdateDocumentMetadataView(
                document,
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
