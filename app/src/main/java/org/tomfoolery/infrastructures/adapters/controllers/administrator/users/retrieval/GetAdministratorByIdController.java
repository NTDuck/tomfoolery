package org.tomfoolery.infrastructures.adapters.controllers.administrator.users.retrieval;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.usecases.administrator.users.retrieval.GetAdministratorByIdUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.utils.helpers.adapters.TimestampBiAdapter;
import org.tomfoolery.infrastructures.utils.helpers.adapters.UserIdBiAdapter;

public final class GetAdministratorByIdController implements ThrowableFunction<GetAdministratorByIdController.RequestObject, GetAdministratorByIdController.ViewModel> {
    private final @NonNull GetAdministratorByIdUseCase getAdministratorByIdUseCase;

    public static @NonNull GetAdministratorByIdController of(@NonNull AdministratorRepository administratorRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetAdministratorByIdController(administratorRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetAdministratorByIdController(@NonNull AdministratorRepository administratorRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.getAdministratorByIdUseCase = GetAdministratorByIdUseCase.of(administratorRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws UserIdBiAdapter.UserUuidInvalidException, GetAdministratorByIdUseCase.AuthenticationTokenNotFoundException, GetAdministratorByIdUseCase.AuthenticationTokenInvalidException, GetAdministratorByIdUseCase.UserNotFoundException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.getAdministratorByIdUseCase.apply(requestModel);
        val viewModel = mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static GetAdministratorByIdUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) throws UserIdBiAdapter.UserUuidInvalidException {
        val administratorId = UserIdBiAdapter.parse(requestObject.getAdministratorUuid());

        return GetAdministratorByIdUseCase.Request.of(administratorId);
    }

    private static @NonNull ViewModel mapResponseModelToViewModel(GetAdministratorByIdUseCase.@NonNull Response<Administrator> responseModel) {
        val administrator = responseModel.getUser();
        val administratorAuditTimestamps = administrator.getAudit().getTimestamps();

        return ViewModel.builder()
            .administratorUuid(UserIdBiAdapter.serialize(administrator.getId()))
            .administratorUsername(administrator.getCredentials().getUsername())
            .creationTimestamp(TimestampBiAdapter.serialize(administratorAuditTimestamps.getCreated()))
            .lastLoginTimestamp(administratorAuditTimestamps.getLastLogin() == null ? "null"
                : TimestampBiAdapter.serialize(administratorAuditTimestamps.getLastLogin()))
            .lastLogoutTimestamp(administratorAuditTimestamps.getLastLogout() == null ? "null"
                : TimestampBiAdapter.serialize(administratorAuditTimestamps.getLastLogout()))
            .build();
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String administratorUuid;
    }

    @Value(staticConstructor = "of")
    @Builder(access = AccessLevel.PRIVATE)
    public static class ViewModel {
        @NonNull String administratorUuid;
        @NonNull String administratorUsername;

        @NonNull String creationTimestamp;
        @NonNull String lastLoginTimestamp;
        @NonNull String lastLogoutTimestamp;
    }
}
