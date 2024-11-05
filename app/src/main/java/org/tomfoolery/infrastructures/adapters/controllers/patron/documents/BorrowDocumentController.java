package org.tomfoolery.infrastructures.adapters.controllers.patron.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.patron.documents.BorrowDocumentUseCase;
import org.tomfoolery.infrastructures.utils.contracts.ThrowableFunctionController;

public class BorrowDocumentController implements ThrowableFunctionController<BorrowDocumentController.RequestObject, BorrowDocumentUseCase.Request, BorrowDocumentUseCase.Response> {
    private final @NonNull BorrowDocumentUseCase useCase;

    private BorrowDocumentController(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.useCase = BorrowDocumentUseCase.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    public static @NonNull BorrowDocumentController of(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new BorrowDocumentController(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public BorrowDocumentUseCase.@NonNull Request getRequestModelFromRequestObject(@NonNull RequestObject requestObject) {
        val documentIdValue = requestObject.getDocumentIdValue();
        val documentId = Document.Id.of(documentIdValue);
        return BorrowDocumentUseCase.Request.of(documentId);
    }

    @Override
    public BorrowDocumentUseCase.@NonNull Response apply(@NonNull RequestObject requestObject) throws BorrowDocumentUseCase.PatronAuthenticationTokenNotFoundException, BorrowDocumentUseCase.PatronAuthenticationTokenInvalidException, BorrowDocumentUseCase.PatronNotFoundException, BorrowDocumentUseCase.DocumentNotFoundException, BorrowDocumentUseCase.DocumentAlreadyBorrowedException {
        val requestModel = getRequestModelFromRequestObject(requestObject);
        val responseModel = this.useCase.apply(requestModel);
        return responseModel;
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String documentIdValue;
    }
}
