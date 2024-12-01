package org.tomfoolery.infrastructures.adapters.controllers.admin.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.usecases.admin.users.persistence.UpdateStaffCredentialsUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;

import java.util.UUID;

public final class UpdateStaffCredentialsController implements ThrowableConsumer<UpdateStaffCredentialsController.RequestObject> {
    private final @NonNull UpdateStaffCredentialsUseCase updateStaffCredentialsUseCase;

    public static @NonNull UpdateStaffCredentialsController of(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new UpdateStaffCredentialsController(staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private UpdateStaffCredentialsController(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        this.updateStaffCredentialsUseCase = UpdateStaffCredentialsUseCase.of(staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws UpdateStaffCredentialsUseCase.AuthenticationTokenNotFoundException, UpdateStaffCredentialsUseCase.AuthenticationTokenInvalidException, UpdateStaffCredentialsUseCase.StaffCredentialsInvalidException, UpdateStaffCredentialsUseCase.StaffNotFoundException {
        val requestModel = requestObject.toRequestModel();
        this.updateStaffCredentialsUseCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String staffId;

        @NonNull String newStaffUsername;
        char @NonNull [] newStaffPassword;

        private UpdateStaffCredentialsUseCase.@NonNull Request toRequestModel() {
            val newStaffPassword = SecureString.of(this.newStaffPassword);
            
            val staffId = Staff.Id.of(UUID.fromString(this.staffId));
            val newStaffCredentials = Staff.Credentials.of(newStaffUsername, newStaffPassword);

            return UpdateStaffCredentialsUseCase.Request.of(staffId, newStaffCredentials);
        }
    }
}
