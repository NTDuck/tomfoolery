package org.tomfoolery.infrastructures.adapters.controllers.user.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.user.documents.ShowDocumentsUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.utils.helpers.dataclasses.ViewableFragmentaryDocument;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class ShowDocumentsController implements ThrowableFunction<ShowDocumentsController.RequestObject, ShowDocumentsController.ViewModel> {
    private final @NonNull ShowDocumentsUseCase showDocumentsUseCase;

    public static @NonNull ShowDocumentsController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        return new ShowDocumentsController(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    private ShowDocumentsController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        this.showDocumentsUseCase = ShowDocumentsUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws ShowDocumentsUseCase.AuthenticationTokenInvalidException, ShowDocumentsUseCase.AuthenticationTokenNotFoundException, ShowDocumentsUseCase.PaginationInvalidException {
        val requestModel = requestObject.toRequestModel();
        val responseModel = this.showDocumentsUseCase.apply(requestModel);
        val viewModel = ViewModel.fromResponseModel(responseModel);

        return viewModel;
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;

        private ShowDocumentsUseCase.@NonNull Request toRequestModel() {
            return ShowDocumentsUseCase.Request.of(pageIndex, maxPageSize);
        }
    }

    @Value
    public static class ViewModel {
        @NonNull List<ViewableFragmentaryDocument> paginatedFragmentaryDocuments;
        @NonNull int pageIndex;
        @NonNull int maxPageIndex;

        private static @NonNull ViewModel fromResponseModel(ShowDocumentsUseCase.@NonNull Response responseModel) {
            val paginatedFragmentaryDocuments = responseModel.getPaginatedFragmentaryDocuments();

            val viewablePaginatedFragmentaryDocuments = StreamSupport.stream(paginatedFragmentaryDocuments.spliterator(), true)
                .map(ViewableFragmentaryDocument::of)
                .collect(Collectors.toUnmodifiableList());

            val pageIndex = paginatedFragmentaryDocuments.getPageIndex();
            val maxPageIndex = paginatedFragmentaryDocuments.getMaxPageIndex();

            return new ViewModel(viewablePaginatedFragmentaryDocuments, pageIndex, maxPageIndex);
        }
    }
}
