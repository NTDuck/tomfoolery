package org.tomfoolery.configurations.monolith.console.views.action.common.documents.retrieval;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.common.documents.retrieval.GetDocumentByIdUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.common.documents.retrieval.GetDocumentByIdController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.TemporaryFileProvider;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.hybrid.documents.HybridDocumentRepository;

import java.io.IOException;

public final class GetDocumentByIdActionView extends UserActionView {
    private final @NonNull GetDocumentByIdController getDocumentByIdController;

    public static @NonNull GetDocumentByIdActionView of(@NonNull IOProvider ioProvider, @NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetDocumentByIdActionView(ioProvider, hybridDocumentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetDocumentByIdActionView(@NonNull IOProvider ioProvider, @NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.getDocumentByIdController = GetDocumentByIdController.of(hybridDocumentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            val viewModel = this.getDocumentByIdController.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (GetDocumentByIdUseCase.AuthenticationTokenNotFoundException | GetDocumentByIdUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (GetDocumentByIdUseCase.DocumentISBNInvalidException | GetDocumentByIdUseCase.DocumentNotFoundException | DocumentCoverImageNotOpenableException exception) {
            this.onException(exception);
        }
    }

    private GetDocumentByIdController.@NonNull RequestObject collectRequestObject() {
        val ISBN = this.ioProvider.readLine(Message.Format.PROMPT, "document ISBN");

        return GetDocumentByIdController.RequestObject.of(ISBN);
    }

    private void displayViewModel(GetDocumentByIdController.@NonNull ViewModel viewModel) throws DocumentCoverImageNotOpenableException {
        val documentCoverImageFilePath = viewModel.getDocumentCoverImageFilePath();

        try {
            TemporaryFileProvider.open(documentCoverImageFilePath);
        } catch (IOException exception) {
            this.ioProvider.writeLine(Message.Format.ERROR, "Failed to open document cover image");
        }

        this.ioProvider.writeLine("""
            Document Details:
            - ISBN 10: %s
            - ISBN 13: %s
            - Title: %s
            - Description: %s
            - Authors: %s
            - Genres: %s
            - Published by %s in %d
            - Rated %f by %d patrons""",
            viewModel.getDocumentISBN_10(),
            viewModel.getDocumentISBN_13(),
            viewModel.getDocumentTitle(),
            viewModel.getDocumentDescription(),
            String.join(", ", viewModel.getDocumentAuthors()),
            String.join(", ", viewModel.getDocumentGenres()),
            viewModel.getDocumentPublisher(),
            viewModel.getDocumentPublishedYear(),
            viewModel.getAverageRating(),
            viewModel.getNumberOfRatings()
        );
    }

    private void onSuccess(GetDocumentByIdController.@NonNull ViewModel viewModel) throws DocumentCoverImageNotOpenableException {
        this.nextViewClass = cachedViewClass;

        this.displayViewModel(viewModel);
    }

    private static class DocumentCoverImageNotOpenableException extends Exception {}
}
