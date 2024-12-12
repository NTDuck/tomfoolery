package org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.retrieval;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.usecases.external.administrator.users.retrieval.ShowPatronAccountsUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

import java.util.List;

public final class ShowPatronAccountsController implements ThrowableFunction<ShowPatronAccountsController.RequestObject, ShowPatronAccountsController.ViewModel> {
    private final @NonNull ShowPatronAccountsUseCase showPatronAccountsUseCase;

    public static @NonNull ShowPatronAccountsController of(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ShowPatronAccountsController(patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ShowPatronAccountsController(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.showPatronAccountsUseCase = ShowPatronAccountsUseCase.of(patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws ShowPatronAccountsUseCase.AuthenticationTokenNotFoundException, ShowPatronAccountsUseCase.AuthenticationTokenInvalidException, ShowPatronAccountsUseCase.PaginationInvalidException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.showPatronAccountsUseCase.apply(requestModel);
        val viewModel = mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static ShowPatronAccountsUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return ShowPatronAccountsUseCase.Request.of(requestObject.getPageIndex(), requestObject.getMaxPageSize());
    }

    private static @NonNull ViewModel mapResponseModelToViewModel(ShowPatronAccountsUseCase.@NonNull Response<Patron> responseModel) {
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
