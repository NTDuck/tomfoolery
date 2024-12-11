package org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.search;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.search.UserSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.usecases.external.administrator.users.search.SearchAdministratorsByUsernameUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.retrieval.GetAdministratorByIdController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class SearchAdministratorsByUsernameController implements ThrowableFunction<SearchAdministratorsByUsernameController.RequestObject, SearchAdministratorsByUsernameController.ViewModel> {
    private final @NonNull SearchAdministratorsByUsernameUseCase searchAdministratorsByUsernameUseCase;

    public static @NonNull SearchAdministratorsByUsernameController of(@NonNull UserSearchGenerator<Administrator> userSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new SearchAdministratorsByUsernameController(userSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private SearchAdministratorsByUsernameController(@NonNull UserSearchGenerator<Administrator> userSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.searchAdministratorsByUsernameUseCase = SearchAdministratorsByUsernameUseCase.of(userSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws SearchAdministratorsByUsernameUseCase.AuthenticationTokenNotFoundException, SearchAdministratorsByUsernameUseCase.AuthenticationTokenInvalidException, SearchAdministratorsByUsernameUseCase.PaginationInvalidException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.searchAdministratorsByUsernameUseCase.apply(requestModel);
        val viewModel = mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static SearchAdministratorsByUsernameUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return SearchAdministratorsByUsernameUseCase.Request.of(requestObject.getSearchTerm(), requestObject.getPageIndex(), requestObject.getMaxPageSize());
    }

    private static @NonNull ViewModel mapResponseModelToViewModel(SearchAdministratorsByUsernameUseCase.@NonNull Response<Administrator> responseModel) {
        val paginatedAdministrators = responseModel.getPaginatedUsers();

        val pageIndex = paginatedAdministrators.getPageIndex();
        val maxPageIndex = paginatedAdministrators.getMaxPageIndex();

        val viewablePaginatedAdministrators = StreamSupport.stream(paginatedAdministrators.spliterator(), true)
            .map(GetAdministratorByIdController.ViewModel::of)
            .collect(Collectors.toUnmodifiableList());

        return ViewModel.of(viewablePaginatedAdministrators, pageIndex, maxPageIndex);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String searchTerm;

        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull List<GetAdministratorByIdController.ViewModel> administrators;

        @Unsigned int pageIndex;
        @Unsigned int maxPageIndex;
    }
}
