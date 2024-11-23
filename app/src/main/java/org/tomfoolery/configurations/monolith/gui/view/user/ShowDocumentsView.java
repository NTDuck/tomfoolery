package org.tomfoolery.configurations.monolith.gui.view.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
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

        } catch (PageIndexInvalidException exception) {
            this.onPageIndexInvalidException();
        } catch (ShowDocumentsUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (ShowDocumentsUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (ShowDocumentsUseCase.PaginationInvalidException exception) {
            this.onPaginationInvalidException();
        }
    }

    private ShowDocumentsController.@NonNull RequestObject collectRequestObject() throws PageIndexInvalidException {
        val rawPageIndex = "1";

        try {
            val pageIndex = Integer.parseUnsignedInt(rawPageIndex);
            return ShowDocumentsController.RequestObject.of(pageIndex, 1000);

        } catch (NumberFormatException exception) {
            throw new PageIndexInvalidException();
        }
    }

    private void onSuccess(ShowDocumentsController.ViewModel viewModel) {
        val pageIndex = viewModel.getPageIndex();
        val maxPageIndex = viewModel.getMaxPageIndex();

        ObservableList<DocumentViewModel> documents = FXCollections.observableArrayList();
        viewModel.getPaginatedFragmentaryDocuments()
                .forEach(fragmentaryDocument -> {
                    String ISBN = fragmentaryDocument.getISBN();
                    String title = fragmentaryDocument.getDocumentTitle();
                    String description = fragmentaryDocument.getDocumentDescription();
                    String yearPublished = String.valueOf(fragmentaryDocument.getDocumentPublishedYear());
                    String authors = String.join(", ", fragmentaryDocument.getDocumentAuthors());
                    String genres = String.join(", ", fragmentaryDocument.getDocumentGenres());

                    documents.add(new DocumentViewModel(
                            1, ISBN, title, authors, genres, description, yearPublished, "", "")
                    );
                });

        documentsTable.getItems().clear();
        documentsTable.setItems(documents);
    }

    private void onPageIndexInvalidException() {
    }

    private void onPaginationInvalidException() {
    }

    private void onAuthenticationTokenInvalidException() {
    }

    private void onAuthenticationTokenNotFoundException() {
    }

    private static class PageIndexInvalidException extends Exception {}

    @AllArgsConstructor
    @Getter
    public static class DocumentViewModel {
        final int index;
        final String ISBN;
        final String title;
        final String authors;
        final String genres;
        final String description;
        final String publishedYear;
        final String created;
        final String lastModified;
    }
}
