package org.tomfoolery.infrastructures.adapters.controllers.common.documents.retrieval;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.common.documents.retrieval.ShowDocumentsUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

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
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.showDocumentsUseCase.apply(requestModel);
        val viewModel = mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static ShowDocumentsUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return ShowDocumentsUseCase.Request.of(requestObject.getPageIndex(), requestObject.getMaxPageSize());
    }

    private static @NonNull ViewModel mapResponseModelToViewModel(ShowDocumentsUseCase.@NonNull Response responseModel) {
        val page = responseModel.getPaginatedDocuments();

        val paginatedDocuments = StreamSupport.stream(page.spliterator(), true)
            .map(GetDocumentByIdController.ViewModel::of)
            .collect(Collectors.toUnmodifiableList());

        val pageIndex = page.getPageIndex();
        val maxPageIndex = page.getMaxPageIndex();

        return ViewModel.of(paginatedDocuments, pageIndex, maxPageIndex);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull List<GetDocumentByIdController.ViewModel> paginatedDocuments;

        @Unsigned int pageIndex;
        @Unsigned int maxPageIndex;
    }
}
