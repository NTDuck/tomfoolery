package org.tomfoolery.infrastructures.adapters.controllers.administrator.users.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.usecases.administrator.users.persistence.UpdateStaffCredentialsUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;
import org.tomfoolery.infrastructures.utils.helpers.adapters.UserIdBiAdapter;

public final class UpdateStaffCredentialsController implements ThrowableConsumer<UpdateStaffCredentialsController.RequestObject> {
    private final @NonNull UpdateStaffCredentialsUseCase updateStaffCredentialsUseCase;

    public static @NonNull UpdateStaffCredentialsController of(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new UpdateStaffCredentialsController(staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private UpdateStaffCredentialsController(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        this.updateStaffCredentialsUseCase = UpdateStaffCredentialsUseCase.of(staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws UserIdBiAdapter.UserUuidInvalidException, UpdateStaffCredentialsUseCase.AuthenticationTokenNotFoundException, UpdateStaffCredentialsUseCase.AuthenticationTokenInvalidException, UpdateStaffCredentialsUseCase.StaffCredentialsInvalidException, UpdateStaffCredentialsUseCase.StaffNotFoundException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        this.updateStaffCredentialsUseCase.accept(requestModel);
    }

    private static UpdateStaffCredentialsUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) throws UserIdBiAdapter.UserUuidInvalidException {
        val staffId = UserIdBiAdapter.parse(requestObject.getStaffUuid());

        val newStaffPassword = SecureString.of(requestObject.getNewStaffPassword());
        val newStaffCredentials = Staff.Credentials.of(requestObject.getNewStaffUsername(), newStaffPassword);

        return UpdateStaffCredentialsUseCase.Request.of(staffId, newStaffCredentials);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String staffUuid;

        @NonNull String newStaffUsername;
        char @NonNull [] newStaffPassword;
    }
}
