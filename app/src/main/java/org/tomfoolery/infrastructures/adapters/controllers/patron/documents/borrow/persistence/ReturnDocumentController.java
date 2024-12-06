package org.tomfoolery.infrastructures.adapters.controllers.patron.documents.borrow.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.patron.documents.borrow.persistence.ReturnDocumentUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.hybrid.documents.HybridDocumentRepository;

public final class ReturnDocumentController implements ThrowableConsumer<ReturnDocumentController.RequestObject> {
    private final @NonNull ReturnDocumentUseCase returnDocumentUseCase;

    public static @NonNull ReturnDocumentController of(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ReturnDocumentController(hybridDocumentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ReturnDocumentController(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.returnDocumentUseCase = ReturnDocumentUseCase.of(hybridDocumentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws ReturnDocumentUseCase.AuthenticationTokenNotFoundException, ReturnDocumentUseCase.AuthenticationTokenInvalidException, ReturnDocumentUseCase.DocumentISBNInvalidException, ReturnDocumentUseCase.DocumentNotFoundException, ReturnDocumentUseCase.DocumentNotBorrowedException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        this.returnDocumentUseCase.accept(requestModel);
    }

    private static ReturnDocumentUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return ReturnDocumentUseCase.Request.of(requestObject.getDocumentISBN());
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String documentISBN;
    }
}
