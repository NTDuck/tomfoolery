package org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.search;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.search.PatronSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.usecases.external.administrator.users.search.SearchPatronsByUsernameUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.retrieval.GetPatronByIdController;

import java.util.List;

public final class SearchPatronsByUsernameController implements ThrowableFunction<SearchPatronsByUsernameController.RequestObject, SearchPatronsByUsernameController.ViewModel> {
    private final @NonNull SearchPatronsByUsernameUseCase searchAdministratorsByUsernameUseCase;

    public static @NonNull SearchPatronsByUsernameController of(@NonNull PatronSearchGenerator patronSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new SearchPatronsByUsernameController(patronSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private SearchPatronsByUsernameController(@NonNull PatronSearchGenerator patronSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.searchAdministratorsByUsernameUseCase = SearchPatronsByUsernameUseCase.of(patronSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
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
        val patronsPage = responseModel.getUsersPage();
        val paginatedPatrons = patronsPage
            .map(GetPatronByIdController.ViewModel::of)
            .toPaginatedList();

        val pageIndex = patronsPage.getPageIndex();
        val maxPageIndex = patronsPage.getMaxPageIndex();

        return ViewModel.of(paginatedPatrons, pageIndex, maxPageIndex);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String searchTerm;

        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull List<GetPatronByIdController.ViewModel> paginatedPatrons;

        @Unsigned int pageIndex;
        @Unsigned int maxPageIndex;
    }
}
