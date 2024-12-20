package org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.search;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.search.AdministratorSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.usecases.external.administrator.users.search.SearchAdministratorsByUsernameUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.retrieval.GetAdministratorByIdController;

import java.util.List;

public final class SearchAdministratorsByUsernameController implements ThrowableFunction<SearchAdministratorsByUsernameController.RequestObject, SearchAdministratorsByUsernameController.ViewModel> {
    private final @NonNull SearchAdministratorsByUsernameUseCase searchAdministratorsByUsernameUseCase;

    public static @NonNull SearchAdministratorsByUsernameController of(@NonNull AdministratorSearchGenerator administratorSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new SearchAdministratorsByUsernameController(administratorSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private SearchAdministratorsByUsernameController(@NonNull AdministratorSearchGenerator administratorSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.searchAdministratorsByUsernameUseCase = SearchAdministratorsByUsernameUseCase.of(administratorSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
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
        val administratorsPage = responseModel.getUsersPage();
        val paginatedAdministrators = administratorsPage
            .map(GetAdministratorByIdController.ViewModel::of)
            .toPaginatedList();

        val pageIndex = administratorsPage.getPageIndex();
        val maxPageIndex = administratorsPage.getMaxPageIndex();

        return ViewModel.of(paginatedAdministrators, pageIndex, maxPageIndex);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String searchTerm;

        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull List<GetAdministratorByIdController.ViewModel> paginatedAdministrators;

        @Unsigned int pageIndex;
        @Unsigned int maxPageIndex;
    }
}
