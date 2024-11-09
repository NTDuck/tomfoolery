package org.tomfoolery.infrastructures.adapters.controllers.patron.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.patron.documents.ShowBorrowedDocumentsUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.utils.helpers.dataclasses.ViewableFragmentaryDocument;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class ShowBorrowedDocumentsController implements ThrowableFunction<ShowBorrowedDocumentsController.RequestObject, ShowBorrowedDocumentsController.ViewModel> {
    private final @NonNull ShowBorrowedDocumentsUseCase showBorrowedDocumentsUseCase;

    public static @NonNull ShowBorrowedDocumentsController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository) {
        return new ShowBorrowedDocumentsController(authenticationTokenGenerator, authenticationTokenRepository, documentRepository, patronRepository);
    }

    private ShowBorrowedDocumentsController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository) {
        this.showBorrowedDocumentsUseCase = ShowBorrowedDocumentsUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository, patronRepository);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws ShowBorrowedDocumentsUseCase.AuthenticationTokenNotFoundException, ShowBorrowedDocumentsUseCase.AuthenticationTokenInvalidException, ShowBorrowedDocumentsUseCase.PatronNotFoundException, ShowBorrowedDocumentsUseCase.PaginationInvalidException {
        val requestModel = requestObject.toRequestModel();
        val responseModel = this.showBorrowedDocumentsUseCase.apply(requestModel);
        val viewModel = ViewModel.fromResponseModel(responseModel);

        return viewModel;
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;

        private ShowBorrowedDocumentsUseCase.@NonNull Request toRequestModel() {
            return ShowBorrowedDocumentsUseCase.Request.of(pageIndex, maxPageSize);
        }
    }

    @Value
    public static class ViewModel {
        @NonNull List<ViewableFragmentaryDocument> paginatedFragmentaryDocuments;
        @NonNull int pageIndex;
        @NonNull int maxPageIndex;

        private static @NonNull ViewModel fromResponseModel(ShowBorrowedDocumentsUseCase.@NonNull Response responseModel) {
            val paginatedFragmentaryDocuments = responseModel.getPaginatedFragmentaryBorrowedDocuments();

            val viewablePaginatedFragmentaryDocuments = StreamSupport.stream(paginatedFragmentaryDocuments.spliterator(), true)
                .map(ViewableFragmentaryDocument::of)
                .collect(Collectors.toUnmodifiableList());

            val pageIndex = paginatedFragmentaryDocuments.getPageIndex();
            val maxPageIndex = paginatedFragmentaryDocuments.getMaxPageIndex();

            return new ViewModel(viewablePaginatedFragmentaryDocuments, pageIndex, maxPageIndex);
        }
    }
}
