package org.tomfoolery.configurations.monolith.gui.view.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.user.documents.ShowDocumentsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.user.documents.ShowDocumentsController;

public class ShowDocumentsView {
    @FXML
    protected TableView<DocumentViewModel> documentsTable;

    protected final @NonNull ShowDocumentsController controller;

    public ShowDocumentsView(@NonNull DocumentRepository documentRepository,
                             @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
                             @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.controller = ShowDocumentsController.of(
                documentRepository,
                authenticationTokenGenerator,
                authenticationTokenRepository
        );
    }

    @FXML
    public void initialize() {
        showDocuments();
    }

    public void showDocuments() {
        try {
            val requestObject = this.collectRequestObject();
            val viewModel = this.controller.apply(requestObject);

            this.onSuccess(viewModel);
        } catch (ShowDocumentsUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (ShowDocumentsUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (ShowDocumentsUseCase.PaginationInvalidException exception) {
            this.onPaginationInvalidException();
        }
    }

    private ShowDocumentsController.@NonNull RequestObject collectRequestObject() {
        return ShowDocumentsController.RequestObject.of(1, 1000);
    }

    private void onSuccess(ShowDocumentsController.ViewModel viewModel) {
        ObservableList<DocumentViewModel> documents = FXCollections.observableArrayList();
        viewModel.getPaginatedFragmentaryDocuments()
                .forEach(fragmentaryDocument -> {
                    String ISBN = fragmentaryDocument.getISBN();
                    String title = fragmentaryDocument.getDocumentTitle();
                    String authors = String.join(", ", fragmentaryDocument.getDocumentAuthors());
                    String genres = String.join(", ", fragmentaryDocument.getDocumentGenres());
                    String description = fragmentaryDocument.getDocumentDescription();
                    String yearPublished = String.valueOf(fragmentaryDocument.getDocumentPublishedYear());
                    String publisher = fragmentaryDocument.getDocumentPublisher();
                    String created = fragmentaryDocument.getLastCreatedTimestamp();
                    String lastModified = fragmentaryDocument.getLastModifiedTimestamp();

                    documents.add(DocumentViewModel.of(
                            ISBN, title, authors, genres, description, yearPublished, publisher, created, lastModified)
                    );
                });

        documentsTable.getItems().clear();
        documentsTable.setItems(documents);
    }

    private void onPaginationInvalidException() {
        Label placeholder = new Label("Our library is currently not having any documents");
        placeholder.setStyle("-fx-font-size: 30;");
        documentsTable.setPlaceholder(new Label());
    }

    private void onAuthenticationTokenInvalidException() {
    }

    private void onAuthenticationTokenNotFoundException() {
    }

    private static class PageIndexInvalidException extends Exception {}

    @Value(staticConstructor = "of")
    public static class DocumentViewModel {
        String ISBN;
        String title;
        String authors;
        String genres;
        String description;
        String publishedYear;
        String publisher;
        String created;
        String lastModified;
    }
}
