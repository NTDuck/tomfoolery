package org.tomfoolery.configurations.monolith.gui.view.patron;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.patron.documents.ShowBorrowedDocumentsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.ShowBorrowedDocumentsController;

import java.util.function.Consumer;

public class ShowBorrowedDocumentsView {
    private final @NonNull ShowBorrowedDocumentsController showBorrowedDocumentsController;

    public ShowBorrowedDocumentsView(@NonNull DocumentRepository documentRepository,
                                     @NonNull PatronRepository patronRepository,
                                     @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
                                     @NonNull AuthenticationTokenRepository authenticationTokenRepository
    ) {
        this.showBorrowedDocumentsController = ShowBorrowedDocumentsController.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
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
            rateDocument(document.getISBN());
        });

        loadDocuments();
    }

    private void addButtonToColumn(TableColumn<DocumentViewModel, Void> column, String buttonText, Consumer<DocumentViewModel> action) {
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

    }

    private void rateDocument(String isbn) {

    }

    private void returnDocument(String isbn) {

    }

    private void loadDocuments() {
        try {
            val requestObject = this.collectShowBorrowedDocumentsRequestObject();
            val viewModel = this.showBorrowedDocumentsController.apply(requestObject);
            this.onShowBorrowedDocumentsSuccess(viewModel);

        } catch (ShowBorrowedDocumentsUseCase.AuthenticationTokenNotFoundException exception) {
            System.err.println("user doesn't exist???");
        } catch (ShowBorrowedDocumentsUseCase.AuthenticationTokenInvalidException exception) {
            System.err.println("some how the auth token is invalid");
        } catch (ShowBorrowedDocumentsUseCase.PatronNotFoundException exception) {
            System.err.println("you are a patron, you don't exist?");
        } catch (ShowBorrowedDocumentsUseCase.PaginationInvalidException exception) {
            System.err.println("no documents borrowed");
        }
    }

    private void onShowBorrowedDocumentsSuccess(ShowBorrowedDocumentsController.@NonNull ViewModel viewModel) {
        ObservableList<DocumentViewModel> documents = FXCollections.observableArrayList();

        viewModel.getPaginatedFragmentaryDocuments().forEach(
        fragmentaryDocument -> {
            String ISBN = fragmentaryDocument.getISBN();
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

    @Value(staticConstructor = "of")
    public static class DocumentViewModel {
        String ISBN;
        String title;
        String authors;
    }
}
