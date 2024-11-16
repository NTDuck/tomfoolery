package org.tomfoolery.infrastructures.adapters.controllers.user.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.user.documents.search.abc.SearchDocumentsUseCase;
import org.tomfoolery.core.usecases.user.documents.search.SearchDocumentsByAuthorSubsequenceUseCase;
import org.tomfoolery.core.usecases.user.documents.search.SearchDocumentsByGenreSubsequenceUseCase;
import org.tomfoolery.core.usecases.user.documents.search.SearchDocumentsByTitleSubsequenceUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.utils.dataclasses.ViewableFragmentaryDocument;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class SearchDocumentsController implements ThrowableFunction<SearchDocumentsController.RequestObject, SearchDocumentsController.ViewModel> {
    private final @NonNull Map<SearchCriterionAndPattern, SearchDocumentsUseCase> searchDocumentsUseCasesBySearchCriteria;

    public static @NonNull SearchDocumentsController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        return new SearchDocumentsController(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    private SearchDocumentsController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        this.searchDocumentsUseCasesBySearchCriteria = Map.of(
            SearchCriterion.TITLE, SearchDocumentsByTitleSubsequenceUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository),
            SearchCriterion.AUTHOR, SearchDocumentsByAuthorSubsequenceUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository),
            SearchCriterion.GENRE, SearchDocumentsByGenreSubsequenceUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository)
        );
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws SearchDocumentsUseCase.AuthenticationTokenNotFoundException, SearchDocumentsUseCase.AuthenticationTokenInvalidException, SearchDocumentsUseCase.PaginationInvalidException {
        val searchCriterion = requestObject.getSearchCriterion();
        val searchDocumentsByCriterionUseCase = this.searchDocumentsUseCasesBySearchCriteria.get(searchCriterion);

        val requestModel = requestObject.toRequestModel();
        val responseModel = searchDocumentsByCriterionUseCase.apply(requestModel);
        val viewModel = ViewModel.fromResponseModel(responseModel);

        return viewModel;
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull SearchCriterion searchCriterion;
        @NonNull SearchPattern searchPattern;

        @NonNull String searchText;
        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;

        private SearchDocumentsUseCase.@NonNull Request toRequestModel() {
            return SearchDocumentsUseCase.Request.of(searchText, pageIndex, maxPageSize);
        }
    }

    @Value
    public static class ViewModel {
        @NonNull List<ViewableFragmentaryDocument> paginatedFragmentaryDocuments;
        @NonNull int pageIndex;
        @NonNull int maxPageIndex;

        private static @NonNull ViewModel fromResponseModel(SearchDocumentsUseCase.@NonNull Response responseModel) {
            val paginatedFragmentaryDocuments = responseModel.getPaginatedFragmentaryDocuments();

            val viewablePaginatedFragmentaryDocuments = StreamSupport.stream(paginatedFragmentaryDocuments.spliterator(), true)
                .map(ViewableFragmentaryDocument::of)
                .collect(Collectors.toUnmodifiableList());

            val pageIndex = paginatedFragmentaryDocuments.getPageIndex();
            val maxPageIndex = paginatedFragmentaryDocuments.getMaxPageIndex();

            return new ViewModel(viewablePaginatedFragmentaryDocuments, pageIndex, maxPageIndex);
        }
    }

    public enum SearchCriterion {
        TITLE, AUTHOR, GENRE,
    }

    public enum SearchPattern {
        PREFIX, SUFFIX, SUBSEQUENCE,
    }

    @Value(staticConstructor = "of")
    private static class SearchCriterionAndPattern {
        @NonNull SearchCriterion searchCriterion;
        @NonNull SearchPattern searchPattern;
    }

    @FunctionalInterface
    private interface SearchDocumentsUseCaseInitializer {
        @NonNull SearchDocumentsUseCase apply(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository);
    }
}
