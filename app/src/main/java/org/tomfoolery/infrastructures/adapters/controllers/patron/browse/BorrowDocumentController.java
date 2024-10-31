package org.tomfoolery.infrastructures.adapters.controllers.patron.browse;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.dataproviders.PatronRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.usecases.external.patron.browse.BorrowDocumentUseCase;
import org.tomfoolery.infrastructures.utils.contracts.ThrowableFunctionController;

public class BorrowDocumentController implements ThrowableFunctionController<BorrowDocumentController.RequestObject, BorrowDocumentUseCase.Request, BorrowDocumentUseCase.Response> {
    private final @NonNull BorrowDocumentUseCase useCase;

    private BorrowDocumentController(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenService authenticationTokenService, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.useCase = BorrowDocumentUseCase.of(documentRepository, patronRepository, authenticationTokenService, authenticationTokenRepository);
    }

    public static @NonNull BorrowDocumentController of(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenService authenticationTokenService, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new BorrowDocumentController(documentRepository, patronRepository, authenticationTokenService, authenticationTokenRepository);
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
