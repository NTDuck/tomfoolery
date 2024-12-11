package org.tomfoolery.infrastructures.adapters.controllers.external.staff.documents.retrieval;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.staff.documents.retrieval.ShowDocumentsWithoutContentUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.adapters.controllers.external.common.documents.retrieval.GetDocumentByIdController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class ShowDocumentsWithoutContentController implements ThrowableFunction<ShowDocumentsWithoutContentController.RequestObject, ShowDocumentsWithoutContentController.ViewModel> {
    private final @NonNull ShowDocumentsWithoutContentUseCase showDocumentsWithoutContentUseCase;
    private final @NonNull FileStorageProvider fileStorageProvider;

    public static @NonNull ShowDocumentsWithoutContentController of(@NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        return new ShowDocumentsWithoutContentController(documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileStorageProvider);
    }

    private ShowDocumentsWithoutContentController(@NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        this.showDocumentsWithoutContentUseCase = ShowDocumentsWithoutContentUseCase.of(documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository);
        this.fileStorageProvider = fileStorageProvider;
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws ShowDocumentsWithoutContentUseCase.AuthenticationTokenInvalidException, ShowDocumentsWithoutContentUseCase.AuthenticationTokenNotFoundException, ShowDocumentsWithoutContentUseCase.PaginationInvalidException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.showDocumentsWithoutContentUseCase.apply(requestModel);
        val viewModel = this.mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static ShowDocumentsWithoutContentUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return ShowDocumentsWithoutContentUseCase.Request.of(requestObject.getPageIndex(), requestObject.getMaxPageSize());
    }

    private @NonNull ViewModel mapResponseModelToViewModel(ShowDocumentsWithoutContentUseCase.@NonNull Response responseModel) {
        val page = responseModel.getPaginatedDocumentsWithoutContent();

        val builder = GetDocumentByIdController.ViewModel.Builder_.with(this.fileStorageProvider);
        val paginatedDocuments = StreamSupport.stream(page.spliterator(), true)
            .map(builder::build)
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
        @NonNull List<GetDocumentByIdController.ViewModel> paginatedDocumentsWithoutContent;

        @Unsigned int pageIndex;
        @Unsigned int maxPageIndex;
    }
}
