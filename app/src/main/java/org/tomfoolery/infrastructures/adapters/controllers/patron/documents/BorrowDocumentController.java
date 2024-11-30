package org.tomfoolery.infrastructures.adapters.controllers.patron.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.documents.borrow.BorrowDocumentUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

public final class BorrowDocumentController implements ThrowableConsumer<BorrowDocumentController.RequestObject> {
    private final @NonNull BorrowDocumentUseCase borrowDocumentUseCase;

    public static @NonNull BorrowDocumentController of(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new BorrowDocumentController(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private BorrowDocumentController(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.borrowDocumentUseCase = BorrowDocumentUseCase.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws BorrowDocumentUseCase.AuthenticationTokenNotFoundException, BorrowDocumentUseCase.AuthenticationTokenInvalidException, BorrowDocumentUseCase.PatronNotFoundException, BorrowDocumentUseCase.DocumentNotFoundException, BorrowDocumentUseCase.DocumentAlreadyBorrowedException {
        val requestModel = requestObject.toRequestModel();
        this.borrowDocumentUseCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String ISBN;

        private BorrowDocumentUseCase.@NonNull Request toRequestModel() {
            val documentId = Document.Id.of(ISBN);

            return BorrowDocumentUseCase.Request.of(documentId);
        }
    }
}
