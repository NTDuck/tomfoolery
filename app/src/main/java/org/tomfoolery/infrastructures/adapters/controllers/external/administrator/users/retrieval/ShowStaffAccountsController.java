package org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.retrieval;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.usecases.external.administrator.users.retrieval.ShowStaffAccountsUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class ShowStaffAccountsController implements ThrowableFunction<ShowStaffAccountsController.RequestObject, ShowStaffAccountsController.ViewModel> {
    private final @NonNull ShowStaffAccountsUseCase showStaffAccountsUseCase;

    public static @NonNull ShowStaffAccountsController of(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ShowStaffAccountsController(staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ShowStaffAccountsController(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.showStaffAccountsUseCase = ShowStaffAccountsUseCase.of(staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws ShowStaffAccountsUseCase.AuthenticationTokenNotFoundException, ShowStaffAccountsUseCase.AuthenticationTokenInvalidException, ShowStaffAccountsUseCase.PaginationInvalidException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.showStaffAccountsUseCase.apply(requestModel);
        val viewModel = mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static ShowStaffAccountsUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return ShowStaffAccountsUseCase.Request.of(requestObject.getPageIndex(), requestObject.getMaxPageSize());
    }

    private static @NonNull ViewModel mapResponseModelToViewModel(ShowStaffAccountsUseCase.@NonNull Response<Staff> responseModel) {
        val paginatedStaff = responseModel.getPaginatedUsers();

        val pageIndex = paginatedStaff.getPageIndex();
        val maxPageIndex = paginatedStaff.getMaxPageIndex();

        val viewablePaginatedStaff = StreamSupport.stream(paginatedStaff.spliterator(), true)
            .map(GetStaffByIdController.ViewModel::of)
            .collect(Collectors.toUnmodifiableList());

        return ViewModel.of(viewablePaginatedStaff, pageIndex, maxPageIndex);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull List<GetStaffByIdController.ViewModel> staff;

        @Unsigned int pageIndex;
        @Unsigned int maxPageIndex;
    }
}
