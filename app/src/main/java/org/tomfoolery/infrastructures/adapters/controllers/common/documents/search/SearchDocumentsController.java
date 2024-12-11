package org.tomfoolery.infrastructures.adapters.controllers.common.documents.search;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.common.documents.search.*;
import org.tomfoolery.core.usecases.common.documents.search.abc.SearchDocumentsUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.adapters.controllers.common.documents.retrieval.GetDocumentByIdController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class SearchDocumentsController implements ThrowableFunction<SearchDocumentsController.RequestObject, SearchDocumentsController.ViewModel> {
    private final @NonNull Map<SearchCriterion, SearchDocumentsUseCase> searchDocumentsUseCasesBySearchCriteria;
    private final @NonNull FileStorageProvider fileStorageProvider;

    public static @NonNull SearchDocumentsController of(@NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        return new SearchDocumentsController(documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository, fileStorageProvider);
    }

    private SearchDocumentsController(@NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        Map<SearchCriterion, SearchDocumentsUseCaseInitializer> searchDocumentsUseCaseInitializersBySearchCriteriaAndPatterns = Map.of(
            SearchCriterion.TITLE, SearchDocumentsByTitleUseCase::of,
            SearchCriterion.AUTHOR, SearchDocumentsByAuthorUseCase::of,
            SearchCriterion.GENRE, SearchDocumentsByGenreUseCase::of
        );

        this.searchDocumentsUseCasesBySearchCriteria = searchDocumentsUseCaseInitializersBySearchCriteriaAndPatterns.entrySet().parallelStream()
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey,
                entry -> entry.getValue().apply(documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository)
            ));
        this.fileStorageProvider = fileStorageProvider;
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws SearchDocumentsUseCase.AuthenticationTokenNotFoundException, SearchDocumentsUseCase.AuthenticationTokenInvalidException, SearchDocumentsUseCase.PaginationInvalidException {
        val searchCriterion = requestObject.getSearchCriterion();
        val searchDocumentsByCriterionUseCase = this.searchDocumentsUseCasesBySearchCriteria.get(searchCriterion);

        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = searchDocumentsByCriterionUseCase.apply(requestModel);
        val viewModel = this.mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static SearchDocumentsUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return SearchDocumentsUseCase.Request.of(requestObject.getSearchText(), requestObject.getPageIndex(), requestObject.getMaxPageSize());
    }

    private @NonNull ViewModel mapResponseModelToViewModel(SearchDocumentsUseCase.@NonNull Response responseModel) {
        val page = responseModel.getPaginatedDocuments();

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
        @NonNull SearchCriterion searchCriterion;
        @NonNull String searchText;

        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull List<GetDocumentByIdController.ViewModel> paginatedDocuments;

        @Unsigned int pageIndex;
        @Unsigned int maxPageIndex;
    }

    public enum SearchCriterion {
        TITLE, AUTHOR, GENRE,
    }

    @FunctionalInterface
    private interface SearchDocumentsUseCaseInitializer {
        @NonNull SearchDocumentsUseCase apply(@NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository);
    }
}
