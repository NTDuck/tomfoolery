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
import org.tomfoolery.core.usecases.external.patron.documents.borrow.persistence.BorrowDocumentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.patron.documents.borrow.persistence.BorrowDocumentController;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.hybrid.documents.HybridDocumentRepository;

public final class BorrowDocumentActionView extends UserActionView {
    private final @NonNull BorrowDocumentController borrowDocumentController;

    public static @NonNull BorrowDocumentActionView of(@NonNull IOProvider ioProvider, @NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new BorrowDocumentActionView(ioProvider, hybridDocumentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private BorrowDocumentActionView(@NonNull IOProvider ioProvider, @NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.borrowDocumentController = BorrowDocumentController.of(hybridDocumentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            this.borrowDocumentController.accept(requestObject);
            this.onSuccess();

        } catch (BorrowDocumentUseCase.AuthenticationTokenNotFoundException | BorrowDocumentUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (BorrowDocumentUseCase.DocumentISBNInvalidException | BorrowDocumentUseCase.DocumentNotFoundException | BorrowDocumentUseCase.DocumentBorrowLimitExceeded | BorrowDocumentUseCase.DocumentAlreadyBorrowedException exception) {
            this.onException(exception);
        }
    }

    private BorrowDocumentController.@NonNull RequestObject collectRequestObject() {
        val ISBN = this.ioProvider.readLine(Message.Format.PROMPT, "document ISBN");

        return BorrowDocumentController.RequestObject.of(ISBN);
    }

    private void onSuccess() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Document borrowed");
    }
}
