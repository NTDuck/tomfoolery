package org.tomfoolery.infrastructures.adapters.controllers.patron.documents.borrow.retrieval;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.patron.documents.borrow.retrieval.GetDocumentBorrowStatusUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

public final class GetDocumentBorrowStatusController implements ThrowableFunction<GetDocumentBorrowStatusController.RequestObject, GetDocumentBorrowStatusController.ViewModel> {
    private final @NonNull GetDocumentBorrowStatusUseCase getDocumentBorrowStatusUseCase;

    public static @NonNull GetDocumentBorrowStatusController of(@NonNull DocumentRepository documentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetDocumentBorrowStatusController(documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetDocumentBorrowStatusController(@NonNull DocumentRepository documentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.getDocumentBorrowStatusUseCase = GetDocumentBorrowStatusUseCase.of(documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws GetDocumentBorrowStatusUseCase.AuthenticationTokenNotFoundException, GetDocumentBorrowStatusUseCase.AuthenticationTokenInvalidException, GetDocumentBorrowStatusUseCase.DocumentISBNInvalidException, GetDocumentBorrowStatusUseCase.DocumentNotFoundException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.getDocumentBorrowStatusUseCase.apply(requestModel);
        val viewModel = mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static GetDocumentBorrowStatusUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return GetDocumentBorrowStatusUseCase.Request.of(requestObject.getDocumentISBN());
    }

    private static @NonNull ViewModel mapResponseModelToViewModel(GetDocumentBorrowStatusUseCase.@NonNull Response responseModel) {
        return ViewModel.of(responseModel.isDocumentBorrowed());
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String documentISBN;
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        boolean isDocumentBorrowed;
    }
}
