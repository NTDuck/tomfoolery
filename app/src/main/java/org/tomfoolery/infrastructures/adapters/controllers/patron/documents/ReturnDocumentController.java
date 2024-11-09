package org.tomfoolery.infrastructures.adapters.controllers.patron.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.patron.documents.ReturnDocumentUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

public final class ReturnDocumentController implements ThrowableConsumer<ReturnDocumentController.RequestObject> {
    private final @NonNull ReturnDocumentUseCase returnDocumentUseCase;

    public static @NonNull ReturnDocumentController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository) {
        return new ReturnDocumentController(authenticationTokenGenerator, authenticationTokenRepository, documentRepository, patronRepository);
    }

    private ReturnDocumentController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository) {
        this.returnDocumentUseCase = ReturnDocumentUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository, patronRepository);
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
