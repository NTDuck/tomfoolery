package org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.retrieval;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.usecases.external.administrator.users.retrieval.ShowAdministratorAccountsUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

import java.util.List;

public final class ShowAdministratorAccountsController implements ThrowableFunction<ShowAdministratorAccountsController.RequestObject, ShowAdministratorAccountsController.ViewModel> {
    private final @NonNull ShowAdministratorAccountsUseCase showAdministratorAccountsUseCase;

    public static @NonNull ShowAdministratorAccountsController of(@NonNull AdministratorRepository administratorRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ShowAdministratorAccountsController(administratorRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ShowAdministratorAccountsController(@NonNull AdministratorRepository administratorRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.showAdministratorAccountsUseCase = ShowAdministratorAccountsUseCase.of(administratorRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws ShowAdministratorAccountsUseCase.AuthenticationTokenNotFoundException, ShowAdministratorAccountsUseCase.AuthenticationTokenInvalidException, ShowAdministratorAccountsUseCase.PaginationInvalidException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.showAdministratorAccountsUseCase.apply(requestModel);
        val viewModel = mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static ShowAdministratorAccountsUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return ShowAdministratorAccountsUseCase.Request.of(requestObject.getPageIndex(), requestObject.getMaxPageSize());
    }

    private static @NonNull ViewModel mapResponseModelToViewModel(ShowAdministratorAccountsUseCase.@NonNull Response<Administrator> responseModel) {
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
