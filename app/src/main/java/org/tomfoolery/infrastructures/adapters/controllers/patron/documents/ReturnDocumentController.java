package org.tomfoolery.infrastructures.adapters.controllers.patron.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.patron.documents.ReturnDocumentUseCase;
import org.tomfoolery.infrastructures.utils.contracts.ThrowableConsumerController;

public class ReturnDocumentController implements ThrowableConsumerController<ReturnDocumentController.RequestObject, ReturnDocumentUseCase.Request> {
    private final @NonNull ReturnDocumentUseCase useCase;

    private ReturnDocumentController(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.useCase = ReturnDocumentUseCase.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    public static @NonNull ReturnDocumentController of(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ReturnDocumentController(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public ReturnDocumentUseCase.@NonNull Request getRequestModelFromRequestObject(@NonNull RequestObject requestObject) {
        val documentIdValue = requestObject.getDocumentIdValue();
        val documentId = Document.Id.of(documentIdValue);
        return ReturnDocumentUseCase.Request.of(documentId);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws ReturnDocumentUseCase.PatronAuthenticationTokenNotFoundException, ReturnDocumentUseCase.PatronAuthenticationTokenInvalidException, ReturnDocumentUseCase.PatronNotFoundException, ReturnDocumentUseCase.DocumentNotFoundException, ReturnDocumentUseCase.DocumentNotBorrowedException {
        val requestModel = getRequestModelFromRequestObject(requestObject);
        this.useCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String documentIdValue;
    }
}
