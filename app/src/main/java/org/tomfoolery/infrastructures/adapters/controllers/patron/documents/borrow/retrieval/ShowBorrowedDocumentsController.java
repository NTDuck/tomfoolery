package org.tomfoolery.infrastructures.adapters.controllers.patron.documents.borrow.retrieval;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.patron.documents.borrow.retrieval.ShowBorrowedDocumentsUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.adapters.controllers.common.documents.retrieval.GetDocumentByIdController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class ShowBorrowedDocumentsController implements ThrowableFunction<ShowBorrowedDocumentsController.RequestObject, ShowBorrowedDocumentsController.ViewModel> {
    private final @NonNull ShowBorrowedDocumentsUseCase showBorrowedDocumentsUseCase;

    public static @NonNull ShowBorrowedDocumentsController of(@NonNull DocumentRepository documentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ShowBorrowedDocumentsController(documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ShowBorrowedDocumentsController(@NonNull DocumentRepository documentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.showBorrowedDocumentsUseCase = ShowBorrowedDocumentsUseCase.of(documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws ShowBorrowedDocumentsUseCase.AuthenticationTokenInvalidException, ShowBorrowedDocumentsUseCase.AuthenticationTokenNotFoundException, ShowBorrowedDocumentsUseCase.PaginationInvalidException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.showBorrowedDocumentsUseCase.apply(requestModel);
        val viewModel = mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static ShowBorrowedDocumentsUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return ShowBorrowedDocumentsUseCase.Request.of(requestObject.getPageIndex(), requestObject.getMaxPageSize());
    }

    private static @NonNull ViewModel mapResponseModelToViewModel(ShowBorrowedDocumentsUseCase.@NonNull Response responseModel) {
        val page = responseModel.getPaginatedBorrowedDocuments();

        val paginatedBorrowedDocuments = StreamSupport.stream(page.spliterator(), true)
            .map(GetDocumentByIdController.ViewModel::of)
            .collect(Collectors.toUnmodifiableList());

        val pageIndex = page.getPageIndex();
        val maxPageIndex = page.getMaxPageIndex();

        return ViewModel.of(paginatedBorrowedDocuments, pageIndex, maxPageIndex);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull List<GetDocumentByIdController.ViewModel> paginatedBorrowedDocuments;

        @Unsigned int pageIndex;
        @Unsigned int maxPageIndex;
    }
}
