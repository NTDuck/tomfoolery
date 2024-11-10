package org.tomfoolery.infrastructures.adapters.controllers.user.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.user.abc.SearchDocumentsUseCase;
import org.tomfoolery.core.usecases.user.documents.search.SearchDocumentsByAuthorUseCase;
import org.tomfoolery.core.usecases.user.documents.search.SearchDocumentsByGenreUseCase;
import org.tomfoolery.core.usecases.user.documents.search.SearchDocumentsByTitleUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.utils.helpers.dataclasses.ViewableFragmentaryDocument;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class SearchDocumentsByCriterionController implements ThrowableFunction<SearchDocumentsByCriterionController.RequestObject, SearchDocumentsByCriterionController.ViewModel> {
    private final @NonNull Map<SearchCriterion, SearchDocumentsUseCase> searchDocumentsByCriterionUseCasesBySearchCriteria;

    public static @NonNull SearchDocumentsByCriterionController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        return new SearchDocumentsByCriterionController(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    private SearchDocumentsByCriterionController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        this.searchDocumentsByCriterionUseCasesBySearchCriteria = Map.of(
            SearchCriterion.TITLE, SearchDocumentsByTitleUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository),
            SearchCriterion.AUTHOR, SearchDocumentsByAuthorUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository),
            SearchCriterion.GENRE, SearchDocumentsByGenreUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository)
        );
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws SearchDocumentsUseCase.AuthenticationTokenNotFoundException, SearchDocumentsUseCase.AuthenticationTokenInvalidException, SearchDocumentsUseCase.PaginationInvalidException {
        val searchCriterion = requestObject.getSearchCriterion();
        val searchDocumentsByCriterionUseCase = this.searchDocumentsByCriterionUseCasesBySearchCriteria.get(searchCriterion);

        val requestModel = requestObject.toRequestModel();
        val responseModel = searchDocumentsByCriterionUseCase.apply(requestModel);
        val viewModel = ViewModel.fromResponseModel(responseModel);

        return viewModel;
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull SearchCriterion searchCriterion;
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
}
