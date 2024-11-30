package org.tomfoolery.infrastructures.adapters.controllers.user.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.documents.retrieval.ShowDocumentsUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.utils.dataclasses.ViewableFragmentaryDocument;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class ShowDocumentsController implements ThrowableFunction<ShowDocumentsController.RequestObject, ShowDocumentsController.ViewModel> {
    private final @NonNull ShowDocumentsUseCase showDocumentsUseCase;

    public static @NonNull ShowDocumentsController of(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ShowDocumentsController(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ShowDocumentsController(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.showDocumentsUseCase = ShowDocumentsUseCase.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
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
        @Unsigned int pageIndex;
        @Unsigned int maxPageIndex;

        private static @NonNull ViewModel fromResponseModel(ShowDocumentsUseCase.@NonNull Response responseModel) {
            val paginatedFragmentaryDocuments = responseModel.getPaginatedDocumentsWithoutContent();

            val viewablePaginatedFragmentaryDocuments = StreamSupport.stream(paginatedFragmentaryDocuments.spliterator(), true)
                .map(ViewableFragmentaryDocument::of)
                .collect(Collectors.toUnmodifiableList());

            val pageIndex = paginatedFragmentaryDocuments.getPageIndex();
            val maxPageIndex = paginatedFragmentaryDocuments.getMaxPageIndex();

            return new ViewModel(viewablePaginatedFragmentaryDocuments, pageIndex, maxPageIndex);
        }
    }
}
