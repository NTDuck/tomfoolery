package org.tomfoolery.configurations.monolith.gui.view.patron.actions.documents;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.relations.DocumentContent;
import org.tomfoolery.core.usecases.patron.documents.borrow.persistence.ReturnDocumentUseCase;
import org.tomfoolery.core.usecases.patron.documents.borrow.retrieval.ReadBorrowedDocumentUseCase;
import org.tomfoolery.core.usecases.patron.documents.borrow.retrieval.ShowBorrowedDocumentsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.borrow.persistence.ReturnDocumentController;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.borrow.retrieval.ReadBorrowedDocumentController;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.borrow.retrieval.ShowBorrowedDocumentsController;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ShowBorrowedDocumentsView {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final @NonNull ShowBorrowedDocumentsController showBorrowedDocumentsController;
    private final @NonNull ReadBorrowedDocumentController readBorrowedDocumentController;
    private final @NonNull ReturnDocumentController returnDocumentController;

    public ShowBorrowedDocumentsView(@NonNull DocumentRepository documentRepository,
                                     @NonNull DocumentContentRepository contentRepository,
                                     @NonNull BorrowingSessionRepository borrowingSessionRepository,
                                     @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
                                     @NonNull AuthenticationTokenRepository authenticationTokenRepository
    ) {
        this.showBorrowedDocumentsController = ShowBorrowedDocumentsController.of(documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
        this.readBorrowedDocumentController = ReadBorrowedDocumentController.of(documentRepository, contentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
        this.returnDocumentController = ReturnDocumentController.of(documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @FXML
    private TableView<DocumentViewModel> documentsTable;

    @FXML
    private TableColumn<DocumentViewModel, Integer> numberColumn;

    @FXML
    private TableColumn<DocumentViewModel, Void> readColumn;

    @FXML
    private TableColumn<DocumentViewModel, Void> rateColumn;

    @FXML
    private TableColumn<DocumentViewModel, Void> returnColumn;

    @FXML
    public void initialize() {
        addButtonToColumn(readColumn, "Read", (DocumentViewModel document) -> {
            readDocument(document.getISBN());
        });

        addButtonToColumn(returnColumn, "Return", (DocumentViewModel document) -> {
            returnDocument(document.getISBN());
        });

        addButtonToColumn(rateColumn, "Rate", (DocumentViewModel document) -> {
            openRatingView(document.getISBN());
        });

        loadDocuments();
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

    private void readDocument(String isbn) {
        val requestObject = this.collectReadBorrowedDocumentRequestObject(isbn);

        try {
            val viewModel = this.readBorrowedDocumentController.apply(requestObject);
            this.onReadingSuccess(viewModel);
        } catch (ReadBorrowedDocumentUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (ReadBorrowedDocumentUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (ReadBorrowedDocumentController.DocumentContentUnavailable exception) {
            this.onDocumentContentUnavailable();
        } catch (ReadBorrowedDocumentUseCase.DocumentNotFoundException |
                 ReadBorrowedDocumentUseCase.DocumentNotBorrowedException | IOException e) {
            throw new RuntimeException(e);
        } catch (ReadBorrowedDocumentUseCase.DocumentISBNInvalidException e) {
            throw new RuntimeException(e);
        } catch (ReadBorrowedDocumentUseCase.DocumentContentNotFoundException e) {
            throw new RuntimeException(e);
        } catch (ReadBorrowedDocumentUseCase.DocumentOverdueException e) {
            throw new RuntimeException(e);
        }
    }

    private void onReadingSuccess(ReadBorrowedDocumentController.@NonNull ViewModel viewModel) throws IOException {
        val documentContentFilePath = viewModel.getDocumentContentFilePath();
        openPdf(documentContentFilePath);
    }

    private void openPdf(String filePath) {
        executor.submit(() -> {
            File pdfFile = new File(filePath);
            if (pdfFile.exists()) {
                try {
                    Desktop.getDesktop().open(pdfFile);
                } catch (IOException e) {
                    Platform.runLater(() -> {
                        System.err.println("Error opening pdf: " + e.getMessage());
                    });
                }
            } else {
                Platform.runLater(() -> {
                    System.err.println("File not found" + filePath);
                });
            }
        });
    }

    private ReadBorrowedDocumentController.@NonNull RequestObject collectReadBorrowedDocumentRequestObject(String isbn) {
        return ReadBorrowedDocumentController.RequestObject.of(isbn);
    }

    @SneakyThrows
    private void openRatingView(String isbn) {
        RateDocumentView rateDocumentView = new RateDocumentView(
                isbn,
                StageManager.getInstance().getDocumentRepository(),
                StageManager.getInstance().getReviewRepository(),
                StageManager.getInstance().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getAuthenticationTokenRepository()
        );

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Patron/RateDocumentView.fxml"));
        loader.setController(rateDocumentView);
        AnchorPane root = loader.load();
        StageManager.getInstance().getRootStackPane().getChildren().add(root);
    }

    private void returnDocument(String isbn) {
        val requestObject = this.collectRequestObject(isbn);

        try {
            this.returnDocumentController.accept(requestObject);
            StageManager.getInstance().loadPatronView(StageManager.ContentType.PATRON_SHOW_BORROWED); // refresh the view
        } catch (ReturnDocumentUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (ReturnDocumentUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (ReturnDocumentUseCase.DocumentNotBorrowedException |
                 ReturnDocumentUseCase.DocumentNotFoundException exception) {
            System.err.println("How did this happens?");
        } catch (ReturnDocumentUseCase.DocumentISBNInvalidException e) {
            throw new RuntimeException(e);
        }
    }

    private ReturnDocumentController.@NonNull RequestObject collectRequestObject(String isbn) {
        return ReturnDocumentController.RequestObject.of(isbn);
    }

    private void loadDocuments() {
        try {
            val requestObject = this.collectShowBorrowedDocumentsRequestObject();
            val viewModel = this.showBorrowedDocumentsController.apply(requestObject);
            this.onShowBorrowedDocumentsSuccess(viewModel);

        } catch (ShowBorrowedDocumentsUseCase.AuthenticationTokenNotFoundException exception) {
            onAuthenticationTokenNotFoundException();
        } catch (ShowBorrowedDocumentsUseCase.AuthenticationTokenInvalidException exception) {
            onAuthenticationTokenInvalidException();
        } catch (ShowBorrowedDocumentsUseCase.PaginationInvalidException exception) {
        }
    }

    private void onShowBorrowedDocumentsSuccess(ShowBorrowedDocumentsController.@NonNull ViewModel viewModel) {
        ObservableList<DocumentViewModel> documents = FXCollections.observableArrayList();

        viewModel.getPaginatedBorrowedDocuments().forEach(
        fragmentaryDocument -> {
            String ISBN = fragmentaryDocument.getDocumentISBN_13();
            String documentTitle = fragmentaryDocument.getDocumentTitle();
            String documentAuthors = String.join(", ", fragmentaryDocument.getDocumentAuthors());

            DocumentViewModel documentViewModel = DocumentViewModel.of(ISBN, documentTitle, documentAuthors);
            documents.add(documentViewModel);
        });

        documentsTable.getItems().clear();
        documentsTable.setItems(documents);
    }

    public ShowBorrowedDocumentsController.@NonNull RequestObject collectShowBorrowedDocumentsRequestObject() {
        return ShowBorrowedDocumentsController.RequestObject.of(1, 30);
    }

    private void onAuthenticationTokenNotFoundException() {
        System.err.println("user doesn't exist???");
    }

    private void onAuthenticationTokenInvalidException() {
        System.err.println("some how the auth token is invalid");
    }

    private void onDocumentContentUnavailable() {
        System.err.println("Document content unavailable");
    }

    @Value(staticConstructor = "of")
    public static class DocumentViewModel {
        String ISBN;
        String title;
        String authors;
    }
}
