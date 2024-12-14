package org.tomfoolery.configurations.monolith.gui.view.user.documents;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.infrastructures.adapters.controllers.external.common.documents.retrieval.GetDocumentByIdController;
import org.tomfoolery.infrastructures.adapters.controllers.external.common.documents.retrieval.ShowDocumentsController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;

public abstract class ShowDocumentsView {
    @FXML
    protected TableView<DocumentViewModel> documentsTable;

    protected final @NonNull ShowDocumentsController controller;

    public ShowDocumentsView(@NonNull DocumentRepository documentRepository,
                             @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
                             @NonNull AuthenticationTokenRepository authenticationTokenRepository,
                             @NonNull FileStorageProvider fileStorageProvider
    ) {
        this.controller = ShowDocumentsController.of(
                documentRepository,
                authenticationTokenGenerator,
                authenticationTokenRepository,
                fileStorageProvider
        );
    }

    @FXML
    public void initialize() {
        showDocuments();
    }

    public abstract void showDocuments();

    public void onPaginationInvalidException() {
        System.err.println("No documents found");
    }

    public void onAuthenticationTokenInvalidException() {
        StageManager.getInstance().openLoginMenu();
    }

    public void onAuthenticationTokenNotFoundException() {
        StageManager.getInstance().openLoginMenu();
    }

    @Getter
    public static class DocumentViewModel {
        String documentISBN_10;
        String documentISBN_13;

        String createdTimestamp;
        String lastModifiedTimestamp;
        String createdByStaffId;
        String lastModifiedByStaffId;

        String documentTitle;
        String documentDescription;

        String documentAuthors;
        String documentGenres;

        String documentPublishedYear;
        String documentPublisher;

        String averageRating;
        String numberOfRatings;

        String documentCoverImageFilePath;

        public DocumentViewModel(GetDocumentByIdController.@NonNull ViewModel viewModel) {
            this.documentISBN_10 = viewModel.getDocumentISBN_10();
            this.documentISBN_13 = viewModel.getDocumentISBN_13();
            this.createdTimestamp = viewModel.getCreatedTimestamp();
            this.lastModifiedTimestamp = viewModel.getLastModifiedTimestamp();
            this.createdByStaffId = viewModel.getCreatedByStaffId();
            this.lastModifiedByStaffId = viewModel.getLastModifiedByStaffId();
            this.documentTitle = viewModel.getDocumentTitle();
            this.documentDescription = viewModel.getDocumentDescription();
            this.documentAuthors = String.join(", ", viewModel.getDocumentAuthors());
            this.documentGenres = String.join(", ", viewModel.getDocumentGenres());
            this.documentPublishedYear = String.valueOf(viewModel.getDocumentPublishedYear());
            this.documentPublisher = viewModel.getDocumentPublisher();
            this.averageRating = String.valueOf(viewModel.getAverageRating());
            this.numberOfRatings = String.valueOf(viewModel.getNumberOfRatings());
            this.documentCoverImageFilePath = viewModel.getDocumentCoverImageFilePath();
        }
    }
}
