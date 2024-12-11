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
import org.tomfoolery.core.usecases.external.patron.documents.review.persistence.AddDocumentReviewUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.patron.documents.review.persistence.AddDocumentReviewController;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.hybrid.documents.HybridDocumentRepository;

public final class AddDocumentReviewActionView extends UserActionView {
    private final @NonNull AddDocumentReviewController addDocumentReviewController;

    public static @NonNull AddDocumentReviewActionView of(@NonNull IOProvider ioProvider, @NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull ReviewRepository reviewRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new AddDocumentReviewActionView(ioProvider, hybridDocumentRepository, reviewRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private AddDocumentReviewActionView(@NonNull IOProvider ioProvider, @NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull ReviewRepository reviewRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.addDocumentReviewController = AddDocumentReviewController.of(hybridDocumentRepository, reviewRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            this.addDocumentReviewController.accept(requestObject);
            this.onSuccess();

        } catch (AddDocumentReviewUseCase.AuthenticationTokenNotFoundException | AddDocumentReviewUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (RatingInvalidException | AddDocumentReviewUseCase.DocumentISBNInvalidException | AddDocumentReviewUseCase.DocumentNotFoundException | AddDocumentReviewUseCase.RatingInvalidException | AddDocumentReviewUseCase.ReviewAlreadyExistsException exception) {
            this.onException(exception);
        }
    }

    private AddDocumentReviewController.@NonNull RequestObject collectRequestObject() throws RatingInvalidException {
        val ISBN = this.ioProvider.readLine(Message.Format.PROMPT, "document ISBN");
        val rating = this.collectRating();

        return AddDocumentReviewController.RequestObject.of(ISBN, rating);
    }

    private double collectRating() throws RatingInvalidException {
        val rawRating = this.ioProvider.readLine(Message.Format.PROMPT, "rating for document");

        try {
            return Double.parseDouble(rawRating);

        } catch (NumberFormatException exception) {
            throw new RatingInvalidException();
        }
    }

    private void onSuccess() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Added review for document");
    }

    private static class RatingInvalidException extends Exception {}
}
