package org.tomfoolery.infrastructures.adapters.controllers.administrator.users.retrieval;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.usecases.administrator.users.retrieval.GetAdministratorByIdUseCase;
import org.tomfoolery.core.usecases.administrator.users.retrieval.GetStaffByIdUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.utils.helpers.adapters.TimestampBiAdapter;
import org.tomfoolery.infrastructures.utils.helpers.adapters.UserIdBiAdapter;

public final class GetStaffByIdController implements ThrowableFunction<GetStaffByIdController.RequestObject, GetStaffByIdController.ViewModel> {
    private final @NonNull GetStaffByIdUseCase getStaffByIdUseCase;

    public static @NonNull GetStaffByIdController of(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetStaffByIdController(staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetStaffByIdController(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.getStaffByIdUseCase = GetStaffByIdUseCase.of(staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws UserIdBiAdapter.UserUuidInvalidException, GetStaffByIdUseCase.AuthenticationTokenNotFoundException, GetStaffByIdUseCase.AuthenticationTokenInvalidException, GetStaffByIdUseCase.UserNotFoundException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.getStaffByIdUseCase.apply(requestModel);
        val viewModel = mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static GetStaffByIdUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) throws UserIdBiAdapter.UserUuidInvalidException {
        val staffId = UserIdBiAdapter.parse(requestObject.getStaffUuid());

        return GetAdministratorByIdUseCase.Request.of(staffId);
    }

    private static @NonNull ViewModel mapResponseModelToViewModel(GetStaffByIdUseCase.@NonNull Response<Staff> responseModel) {
        return ViewModel.of(responseModel.getUser());
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String staffUuid;
    }

    @Value(staticConstructor = "of")
    @Builder(access = AccessLevel.PRIVATE)
    public static class ViewModel {
        @NonNull String staffUuid;
        @NonNull String staffUsername;

        @NonNull String creationTimestamp;
        @NonNull String lastLoginTimestamp;
        @NonNull String lastLogoutTimestamp;

        public static @NonNull ViewModel of(@NonNull Staff staff) {
            val staffAuditTimestamps = staff.getAudit().getTimestamps();

            return ViewModel.builder()
                .staffUuid(UserIdBiAdapter.serialize(staff.getId()))
                .staffUsername(staff.getCredentials().getUsername())
                .creationTimestamp(TimestampBiAdapter.serialize(staffAuditTimestamps.getCreated()))
                .lastLoginTimestamp(staffAuditTimestamps.getLastLogin() == null ? "null"
                    : TimestampBiAdapter.serialize(staffAuditTimestamps.getLastLogin()))
                .lastLogoutTimestamp(staffAuditTimestamps.getLastLogout() == null ? "null"
                    : TimestampBiAdapter.serialize(staffAuditTimestamps.getLastLogout()))
                .build();
        }
    }
}