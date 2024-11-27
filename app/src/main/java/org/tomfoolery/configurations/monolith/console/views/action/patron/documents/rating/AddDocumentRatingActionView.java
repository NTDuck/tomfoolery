package org.tomfoolery.configurations.monolith.console.views.action.patron.documents.rating;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.PatronSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.patron.documents.rating.AddDocumentRatingUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.rating.AddDocumentRatingController;

public final class AddDocumentRatingActionView extends UserActionView {
    private final @NonNull AddDocumentRatingController controller;

    public static @NonNull AddDocumentRatingActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new AddDocumentRatingActionView(ioProvider, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private AddDocumentRatingActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.controller = AddDocumentRatingController.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            this.controller.accept(requestObject);
            this.onSuccess();

        } catch (RatingInvalidException exception) {
            this.onRatingInvalidException();

        } catch (AddDocumentRatingUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (AddDocumentRatingUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (AddDocumentRatingUseCase.PatronNotFoundException exception) {
            this.onPatronNotFoundException();
        } catch (AddDocumentRatingUseCase.DocumentNotFoundException exception) {
            this.onDocumentNotFoundException();
        } catch (AddDocumentRatingUseCase.RatingValueInvalidException exception) {
            this.onRatingValueInvalidException();
        } catch (AddDocumentRatingUseCase.PatronRatingAlreadyExistsException exception) {
            this.onPatronRatingAlreadyExistsException();
        }
    }

    private AddDocumentRatingController.@NonNull RequestObject collectRequestObject() throws RatingInvalidException {
        val ISBN = this.ioProvider.readLine(Message.Format.PROMPT, "document ISBN");
        val rating = this.collectRating();

        return AddDocumentRatingController.RequestObject.of(ISBN, rating);
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

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Added rating for document");
    }

    private void onRatingInvalidException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Rating must be a number");
    }

    private void onPatronNotFoundException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Patron not found");
    }

    private void onDocumentNotFoundException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Document not found");
    }

    private void onRatingValueInvalidException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Rating must be a number between 0 and 5");
    }

    private void onPatronRatingAlreadyExistsException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Document already rated");
    }

    private static class RatingInvalidException extends Exception {}
}
