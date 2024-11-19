package org.tomfoolery.infrastructures.adapters.controllers.patron.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.usecases.patron.documents.ReadBorrowedDocumentUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

public final class ReadBorrowedDocumentController implements ThrowableFunction<ReadBorrowedDocumentController.RequestObject, ReadBorrowedDocumentController.ViewModel> {
    private final @NonNull ReadBorrowedDocumentUseCase readBorrowedDocumentUseCase;

    public static @NonNull ReadBorrowedDocumentController of(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ReadBorrowedDocumentController(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ReadBorrowedDocumentController(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.readBorrowedDocumentUseCase = ReadBorrowedDocumentUseCase.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws ReadBorrowedDocumentUseCase.AuthenticationTokenNotFoundException, ReadBorrowedDocumentUseCase.AuthenticationTokenInvalidException, ReadBorrowedDocumentUseCase.PatronNotFoundException, ReadBorrowedDocumentUseCase.DocumentNotFoundException, ReadBorrowedDocumentUseCase.DocumentNotBorrowedException {
        val requestModel = requestObject.toRequestModel();
        val responseModel = this.readBorrowedDocumentUseCase.apply(requestModel);
        val viewModel = ViewModel.fromResponseModel(responseModel);

        return viewModel;
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String ISBN;

        private ReadBorrowedDocumentUseCase.@NonNull Request toRequestModel() {
            val documentId = Document.Id.of(ISBN);

            return ReadBorrowedDocumentUseCase.Request.of(documentId);
        }
    }

    @Value
    public static class ViewModel {
        byte @NonNull [] documentContent;

        private static @NonNull ViewModel fromResponseModel(ReadBorrowedDocumentUseCase.@NonNull Response responseModel) {
            val documentContent = responseModel.getDocumentContent();
            val rawDocumentContent = documentContent.getBytes();

            return new ViewModel(rawDocumentContent);
        }
    }
}
