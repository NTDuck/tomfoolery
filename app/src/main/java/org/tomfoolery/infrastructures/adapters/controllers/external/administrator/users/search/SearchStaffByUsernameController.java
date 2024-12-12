package org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.search;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.search.StaffSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.usecases.external.administrator.users.search.SearchPatronsByUsernameUseCase;
import org.tomfoolery.core.usecases.external.administrator.users.search.SearchStaffByUsernameUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.retrieval.GetStaffByIdController;

import java.util.List;

public final class SearchStaffByUsernameController implements ThrowableFunction<SearchStaffByUsernameController.RequestObject, SearchStaffByUsernameController.ViewModel> {
    private final @NonNull SearchStaffByUsernameUseCase searchStaffByUsernameUseCase;

    public static @NonNull SearchStaffByUsernameController of(@NonNull StaffSearchGenerator staffSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new SearchStaffByUsernameController(staffSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private SearchStaffByUsernameController(@NonNull StaffSearchGenerator staffSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.searchStaffByUsernameUseCase = SearchStaffByUsernameUseCase.of(staffSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws SearchPatronsByUsernameUseCase.AuthenticationTokenNotFoundException, SearchPatronsByUsernameUseCase.AuthenticationTokenInvalidException, SearchPatronsByUsernameUseCase.PaginationInvalidException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.searchStaffByUsernameUseCase.apply(requestModel);
        val viewModel = mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static SearchStaffByUsernameUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return SearchStaffByUsernameUseCase.Request.of(requestObject.getSearchTerm(), requestObject.getPageIndex(), requestObject.getMaxPageSize());
    }

    private static @NonNull ViewModel mapResponseModelToViewModel(SearchStaffByUsernameUseCase.@NonNull Response<Staff> responseModel) {
        val staffPage = responseModel.getUsersPage();
        val paginatedStaff = staffPage
            .map(GetStaffByIdController.ViewModel::of)
            .toPaginatedList();

        val pageIndex = staffPage.getPageIndex();
        val maxPageIndex = staffPage.getMaxPageIndex();

        return ViewModel.of(paginatedStaff, pageIndex, maxPageIndex);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String searchTerm;

        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull List<GetStaffByIdController.ViewModel> paginatedStaff;

        @Unsigned int pageIndex;
        @Unsigned int maxPageIndex;
    }
}
