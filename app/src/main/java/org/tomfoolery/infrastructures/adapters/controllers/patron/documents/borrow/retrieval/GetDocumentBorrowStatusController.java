package org.tomfoolery.infrastructures.adapters.controllers.patron.documents.borrow.retrieval;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.patron.documents.borrow.retrieval.GetDocumentBorrowStatusUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.hybrid.documents.HybridDocumentRepository;
import org.tomfoolery.infrastructures.utils.helpers.adapters.TimestampBiAdapter;

public final class GetDocumentBorrowStatusController implements ThrowableFunction<GetDocumentBorrowStatusController.RequestObject, GetDocumentBorrowStatusController.ViewModel> {
    private final @NonNull GetDocumentBorrowStatusUseCase getDocumentBorrowStatusUseCase;

    public static @NonNull GetDocumentBorrowStatusController of(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetDocumentBorrowStatusController(hybridDocumentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetDocumentBorrowStatusController(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.getDocumentBorrowStatusUseCase = GetDocumentBorrowStatusUseCase.of(hybridDocumentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws GetDocumentBorrowStatusUseCase.AuthenticationTokenNotFoundException, GetDocumentBorrowStatusUseCase.AuthenticationTokenInvalidException, GetDocumentBorrowStatusUseCase.DocumentISBNInvalidException, GetDocumentBorrowStatusUseCase.DocumentNotFoundException, GetDocumentBorrowStatusUseCase.DocumentNotBorrowedException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.getDocumentBorrowStatusUseCase.apply(requestModel);
        val viewModel = mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static GetDocumentBorrowStatusUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return GetDocumentBorrowStatusUseCase.Request.of(requestObject.getDocumentISBN());
    }

    private static @NonNull ViewModel mapResponseModelToViewModel(GetDocumentBorrowStatusUseCase.@NonNull Response responseModel) {
        val borrowingSession = responseModel.getBorrowingSession();

        val borrowedTimestamp = TimestampBiAdapter.serialize(borrowingSession.getBorrowedTimestamp());
        val dueTimestamp = TimestampBiAdapter.serialize(borrowingSession.getDueTimestamp());

        return ViewModel.of(borrowedTimestamp, dueTimestamp);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String documentISBN;
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull String borrowedTimestamp;
        @NonNull String dueTimestamp;
    }
}
