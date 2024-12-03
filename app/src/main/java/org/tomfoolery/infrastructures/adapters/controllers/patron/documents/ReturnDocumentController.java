package org.tomfoolery.infrastructures.adapters.controllers.patron.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.patron.documents.borrow.persistence.ReturnDocumentUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

public final class ReturnDocumentController implements ThrowableConsumer<ReturnDocumentController.RequestObject> {
    private final @NonNull ReturnDocumentUseCase returnDocumentUseCase;

    public static @NonNull ReturnDocumentController of(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ReturnDocumentController(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ReturnDocumentController(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.returnDocumentUseCase = ReturnDocumentUseCase.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws ReturnDocumentUseCase.AuthenticationTokenNotFoundException, ReturnDocumentUseCase.AuthenticationTokenInvalidException, ReturnDocumentUseCase.PatronNotFoundException, ReturnDocumentUseCase.DocumentNotFoundException, ReturnDocumentUseCase.DocumentNotBorrowedException {
        val requestModel = requestObject.toRequestModel();
        this.returnDocumentUseCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String ISBN;

        private ReturnDocumentUseCase.@NonNull Request toRequestModel() {
            val documentId = Document.Id.of(ISBN);

            return ReturnDocumentUseCase.Request.of(documentId);
        }
    }
}
