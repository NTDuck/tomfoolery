package org.tomfoolery.infrastructures.adapters.controllers.external.common.documents.retrieval;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.external.common.documents.retrieval.ShowDocumentsUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;

import java.util.List;

public final class ShowDocumentsController implements ThrowableFunction<ShowDocumentsController.RequestObject, ShowDocumentsController.ViewModel> {
    private final @NonNull ShowDocumentsUseCase showDocumentsUseCase;
    private final @NonNull FileStorageProvider fileStorageProvider;

    public static @NonNull ShowDocumentsController of(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        return new ShowDocumentsController(documentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileStorageProvider);
    }

    private ShowDocumentsController(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        this.showDocumentsUseCase = ShowDocumentsUseCase.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
        this.fileStorageProvider = fileStorageProvider;
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws ShowDocumentsUseCase.AuthenticationTokenInvalidException, ShowDocumentsUseCase.AuthenticationTokenNotFoundException, ShowDocumentsUseCase.PaginationInvalidException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.showDocumentsUseCase.apply(requestModel);
        val viewModel = this.mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static ShowDocumentsUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return ShowDocumentsUseCase.Request.of(requestObject.getPageIndex(), requestObject.getMaxPageSize());
    }

    private @NonNull ViewModel mapResponseModelToViewModel(ShowDocumentsUseCase.@NonNull Response responseModel) {
        val documentsPage = responseModel.getDocumentsPage();

        val paginatedDocumentsBuilder = GetDocumentByIdController.ViewModel.Builder_.with(this.fileStorageProvider);
        val paginatedDocuments = documentsPage
            .map(paginatedDocumentsBuilder::build)
            .toPaginatedList();

        val pageIndex = documentsPage.getPageIndex();
        val maxPageIndex = documentsPage.getMaxPageIndex();

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
