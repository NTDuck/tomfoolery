package org.tomfoolery.configurations.monolith.gui.view.admin.actions.documents;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.staff.actions.documents.DocumentDetailsView;
import org.tomfoolery.configurations.monolith.gui.view.user.documents.ShowDocumentsView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.common.documents.retrieval.ShowDocumentsUseCase;
import org.tomfoolery.core.usecases.external.common.documents.search.abc.SearchDocumentsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.common.documents.retrieval.ShowDocumentsController;
import org.tomfoolery.infrastructures.adapters.controllers.external.common.documents.search.SearchDocumentsController;
import org.tomfoolery.infrastructures.adapters.controllers.internal.statistics.GetStatisticsController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;

import java.util.function.Consumer;

public class DocumentsDisplayView extends ShowDocumentsView {
    private final @NonNull GetStatisticsController getStatisticsController = GetStatisticsController.of(
            StageManager.getInstance().getResources().getDocumentRepository(),
            StageManager.getInstance().getResources().getAdministratorRepository(),
            StageManager.getInstance().getResources().getPatronRepository(),
            StageManager.getInstance().getResources().getStaffRepository()
    );

    public DocumentsDisplayView(
            @NonNull DocumentRepository documentRepository,
            @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
            @NonNull AuthenticationTokenRepository authenticationTokenRepository,
            @NonNull FileStorageProvider fileStorageProvider
    ) {
        super(documentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileStorageProvider);
    }

    @FXML
    private TableColumn<DocumentViewModel, Void> showDetailsColumn;

    @FXML
    private Label counterLabel;

    @FXML
    private ComboBox<Integer> pageChooser;

    @FXML
    private TextField searchField;

    @FXML @Override
    public void initialize() {
        addButtonToColumn(showDetailsColumn, "More details", this::showDocumentDetails);

        searchField.setOnAction(event -> {
            searchDocuments();
            pageChooser.getSelectionModel().selectFirst();
        });
        this.setUpPageChooserBehaviour();
        showDocuments();
    }

    private void setUpPageChooserBehaviour() {
        pageChooser.getItems().clear();
        for (int i = 1; i <= Math.ceil((double) this.getStatisticsController.get().getNumberOfDocuments() / 60); ++i) {
            pageChooser.getItems().add(i);
        }
        pageChooser.getSelectionModel().selectFirst();

        pageChooser.setOnAction(event -> {
            if (searchField.getText().isEmpty()) {
                showDocuments();
            }
            if (!searchField.getText().isEmpty()) {
                searchDocuments();
            }
        });
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

        int pageIndex = pageChooser.getSelectionModel().getSelectedItem();
        val requestObject = SearchDocumentsController.RequestObject.of(SearchDocumentsController.SearchCriterion.TITLE, searchField.getText(), pageIndex, 60);

        try {
            val viewModel = controller.apply(requestObject);

            pageChooser.getItems().clear();
            for (int i = 1; i < viewModel.getMaxPageIndex(); ++i) {
                pageChooser.getItems().add(i);
            }
            EventHandler<ActionEvent> currentHandler = pageChooser.getOnAction();
            pageChooser.setOnAction(null);
            pageChooser.setValue(pageIndex);
            pageChooser.setOnAction(currentHandler);

            documentsTable.getItems().clear();
            viewModel.getPaginatedDocuments().forEach(document -> documentsTable.getItems().add(new DocumentViewModel(document)));
            counterLabel.setText(this.getStatisticsController.get().getNumberOfDocuments() + " document(s)");
        } catch (SearchDocumentsUseCase.AuthenticationTokenNotFoundException |
                 SearchDocumentsUseCase.AuthenticationTokenInvalidException e) {
            StageManager.getInstance().openLoginMenu();
        } catch (SearchDocumentsUseCase.PaginationInvalidException _) {
        }
    }

    @Override
    public void showDocuments() {
        try {
            int pageIndex = pageChooser.getSelectionModel().getSelectedItem();
            val requestObject = ShowDocumentsController.RequestObject.of(pageIndex, 60);
            val viewModel = this.controller.apply(requestObject);

            pageChooser.getItems().clear();
            for (int i = 1; i < viewModel.getMaxPageIndex(); ++i) {
                pageChooser.getItems().add(i);
            }
            EventHandler<ActionEvent> currentHandler = pageChooser.getOnAction();
            pageChooser.setOnAction(null);
            pageChooser.setValue(pageIndex);
            pageChooser.setOnAction(currentHandler);

            documentsTable.getItems().clear();
            viewModel.getPaginatedDocuments().forEach(document -> documentsTable.getItems().add(new DocumentViewModel(document)));
            counterLabel.setText(this.getStatisticsController.get().getNumberOfDocuments() + " document(s)");
        } catch (ShowDocumentsUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (ShowDocumentsUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (ShowDocumentsUseCase.PaginationInvalidException exception) {
            this.onPaginationInvalidException();
        }
    }

    @SneakyThrows
    private void showDocumentDetails(DocumentViewModel document) {
        DocumentDetailsView controller = new DocumentDetailsView(document);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Staff/DocumentDetailsView.fxml"));
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
