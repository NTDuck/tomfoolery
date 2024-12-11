package org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.search;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.search.UserSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.usecases.external.administrator.users.search.SearchPatronsByUsernameUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.retrieval.GetPatronByIdController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class SearchPatronsByUsernameController implements ThrowableFunction<SearchPatronsByUsernameController.RequestObject, SearchPatronsByUsernameController.ViewModel> {
    private final @NonNull SearchPatronsByUsernameUseCase searchAdministratorsByUsernameUseCase;

    public static @NonNull SearchPatronsByUsernameController of(@NonNull UserSearchGenerator<Patron> userSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new SearchPatronsByUsernameController(userSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private SearchPatronsByUsernameController(@NonNull UserSearchGenerator<Patron> userSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.searchAdministratorsByUsernameUseCase = SearchPatronsByUsernameUseCase.of(userSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws SearchPatronsByUsernameUseCase.AuthenticationTokenNotFoundException, SearchPatronsByUsernameUseCase.AuthenticationTokenInvalidException, SearchPatronsByUsernameUseCase.PaginationInvalidException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.searchAdministratorsByUsernameUseCase.apply(requestModel);
        val viewModel = mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static SearchPatronsByUsernameUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return SearchPatronsByUsernameUseCase.Request.of(requestObject.getSearchTerm(), requestObject.getPageIndex(), requestObject.getMaxPageSize());
    }

    private static @NonNull ViewModel mapResponseModelToViewModel(SearchPatronsByUsernameUseCase.@NonNull Response<Patron> responseModel) {
        val paginatedPatrons = responseModel.getPaginatedUsers();

        val pageIndex = paginatedPatrons.getPageIndex();
        val maxPageIndex = paginatedPatrons.getMaxPageIndex();

        val viewablePaginatedPatrons = StreamSupport.stream(paginatedPatrons.spliterator(), true)
            .map(GetPatronByIdController.ViewModel::of)
            .collect(Collectors.toUnmodifiableList());

        return ViewModel.of(viewablePaginatedPatrons, pageIndex, maxPageIndex);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String searchTerm;

        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull List<GetPatronByIdController.ViewModel> patrons;

        @Unsigned int pageIndex;
        @Unsigned int maxPageIndex;
    }
}
