package org.tomfoolery.configurations.monolith.console.views.action.patron.documents.review.persistence;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.PatronSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.patron.documents.review.persistence.RemoveDocumentReviewUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.review.persistence.RemoveDocumentReviewController;

public final class RemoveDocumentRatingActionView extends UserActionView {
    private final @NonNull RemoveDocumentReviewController removeDocumentReviewController;

    public static @NonNull RemoveDocumentRatingActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull ReviewRepository reviewRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new RemoveDocumentRatingActionView(ioProvider, documentRepository, reviewRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private RemoveDocumentRatingActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull ReviewRepository reviewRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.removeDocumentReviewController = RemoveDocumentReviewController.of(documentRepository, reviewRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            this.removeDocumentReviewController.accept(requestObject);
            this.onSuccess();

        } catch (RemoveDocumentReviewUseCase.AuthenticationTokenNotFoundException | RemoveDocumentReviewUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (RemoveDocumentReviewUseCase.DocumentISBNInvalidException | RemoveDocumentReviewUseCase.DocumentNotFoundException | RemoveDocumentReviewUseCase.ReviewNotFoundException exception) {
            this.onException(exception);
        }
    }

    private RemoveDocumentReviewController.@NonNull RequestObject collectRequestObject() {
        val ISBN = this.ioProvider.readLine(Message.Format.PROMPT, "document ISBN");

        return RemoveDocumentReviewController.RequestObject.of(ISBN);
    }

    private void onSuccess() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Removed review for document");
    }
}