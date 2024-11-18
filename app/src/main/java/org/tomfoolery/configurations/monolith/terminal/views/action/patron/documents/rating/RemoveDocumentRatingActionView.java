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
import org.tomfoolery.core.usecases.patron.documents.rating.RemoveDocumentRatingUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.rating.RemoveDocumentRatingController;

public final class RemoveDocumentRatingActionView extends UserActionView {
    private final @NonNull RemoveDocumentRatingController controller;

    public static @NonNull RemoveDocumentRatingActionView of(@NonNull IOHandler ioHandler, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new RemoveDocumentRatingActionView(ioHandler, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private RemoveDocumentRatingActionView(@NonNull IOHandler ioHandler, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioHandler);

        this.controller = RemoveDocumentRatingController.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            this.controller.accept(requestObject);
            this.onSuccess();

        } catch (RemoveDocumentRatingUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (RemoveDocumentRatingUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (RemoveDocumentRatingUseCase.PatronNotFoundException exception) {
            this.onPatronNotFoundException();
        } catch (RemoveDocumentRatingUseCase.DocumentNotFoundException exception) {
            this.onDocumentNotFoundException();
        } catch (RemoveDocumentRatingUseCase.PatronRatingNotFoundException exception) {
            this.onPatronRatingNotFoundException();
        }
    }

    private RemoveDocumentRatingController.@NonNull RequestObject collectRequestObject() {
        val ISBN = this.ioHandler.readLine(Message.Format.PROMPT, "document ISBN");

        return RemoveDocumentRatingController.RequestObject.of(ISBN);
    }

    private void onSuccess() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioHandler.writeLine(Message.Format.SUCCESS, "Removed rating for document");
    }

    private void onPatronNotFoundException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioHandler.writeLine(Message.Format.ERROR, "Patron not found");
    }

    private void onDocumentNotFoundException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioHandler.writeLine(Message.Format.ERROR, "Document not found");
    }

    private void onPatronRatingNotFoundException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioHandler.writeLine(Message.Format.ERROR, "Document not rated");
    }
}