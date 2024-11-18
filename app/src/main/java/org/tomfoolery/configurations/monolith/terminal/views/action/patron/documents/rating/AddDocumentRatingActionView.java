package org.tomfoolery.configurations.monolith.terminal.views.action.patron.documents.rating;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.patron.documents.rating.AddDocumentRatingUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.rating.AddDocumentRatingController;

public final class AddDocumentRatingActionView extends UserActionView {
    private final @NonNull AddDocumentRatingController controller;

    public static @NonNull AddDocumentRatingActionView of(@NonNull IOHandler ioHandler, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new AddDocumentRatingActionView(ioHandler, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private AddDocumentRatingActionView(@NonNull IOHandler ioHandler, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioHandler);

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
        val ISBN = this.ioHandler.readLine(Message.Format.PROMPT, "document ISBN");
        val rawRating = this.ioHandler.readLine(Message.Format.PROMPT, "document rating");

        try {
            val rating = Double.parseDouble(rawRating);
            return AddDocumentRatingController.RequestObject.of(ISBN, rating);

        } catch (NumberFormatException exception) {
            throw new RatingInvalidException();
        }
    }

    private void onSuccess() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioHandler.writeLine(Message.Format.SUCCESS, "Added rating for document");
    }

    private void onRatingInvalidException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioHandler.writeLine(Message.Format.ERROR, "Rating must be a number");
    }

    private void onPatronNotFoundException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioHandler.writeLine(Message.Format.ERROR, "Patron not found");
    }

    private void onDocumentNotFoundException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioHandler.writeLine(Message.Format.ERROR, "Document not found");
    }

    private void onRatingValueInvalidException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioHandler.writeLine(Message.Format.ERROR, "Rating must be a number between 0 and 5");
    }

    private void onPatronRatingAlreadyExistsException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioHandler.writeLine(Message.Format.ERROR, "Document already rated");
    }

    private static class RatingInvalidException extends Exception {}
}
