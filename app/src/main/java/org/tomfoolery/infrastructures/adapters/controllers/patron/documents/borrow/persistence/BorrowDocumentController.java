package org.tomfoolery.infrastructures.adapters.controllers.patron.documents.borrow.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.patron.documents.borrow.persistence.BorrowDocumentUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.hybrid.documents.HybridDocumentRepository;

public final class BorrowDocumentController implements ThrowableConsumer<BorrowDocumentController.RequestObject> {
    private final @NonNull BorrowDocumentUseCase borrowDocumentUseCase;

    public static @NonNull BorrowDocumentController of(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new BorrowDocumentController(hybridDocumentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private BorrowDocumentController(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.borrowDocumentUseCase = BorrowDocumentUseCase.of(hybridDocumentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws BorrowDocumentUseCase.AuthenticationTokenNotFoundException, BorrowDocumentUseCase.AuthenticationTokenInvalidException, BorrowDocumentUseCase.DocumentISBNInvalidException, BorrowDocumentUseCase.DocumentNotFoundException, BorrowDocumentUseCase.DocumentBorrowLimitExceeded, BorrowDocumentUseCase.DocumentAlreadyBorrowedException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        this.borrowDocumentUseCase.accept(requestModel);
    }

    private static BorrowDocumentUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return BorrowDocumentUseCase.Request.of(requestObject.getDocumentISBN());
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String documentISBN;
    }
}
