package org.tomfoolery.configurations.monolith.console.views.action.patron.documents.borrow.persistence;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.PatronSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.patron.documents.borrow.persistence.ReturnDocumentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.patron.documents.borrow.persistence.ReturnDocumentController;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.hybrid.documents.HybridDocumentRepository;

public final class ReturnDocumentActionView extends UserActionView {
    private final @NonNull ReturnDocumentController controller;

    public static @NonNull ReturnDocumentActionView of(@NonNull IOProvider ioProvider, @NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ReturnDocumentActionView(ioProvider, hybridDocumentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ReturnDocumentActionView(@NonNull IOProvider ioProvider, @NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.controller = ReturnDocumentController.of(hybridDocumentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            this.controller.accept(requestObject);
            this.onSuccess();

        } catch (ReturnDocumentUseCase.AuthenticationTokenNotFoundException | ReturnDocumentUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (ReturnDocumentUseCase.DocumentISBNInvalidException | ReturnDocumentUseCase.DocumentNotFoundException | ReturnDocumentUseCase.DocumentNotBorrowedException exception) {
            this.onException(exception);
        }
    }

    private ReturnDocumentController.@NonNull RequestObject collectRequestObject() {
        val ISBN = this.ioProvider.readLine(Message.Format.PROMPT, "document ISBN");

        return ReturnDocumentController.RequestObject.of(ISBN);
    }

    private void onSuccess() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Document returned");
    }
}
