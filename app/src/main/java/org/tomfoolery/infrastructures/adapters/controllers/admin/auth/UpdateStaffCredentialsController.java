package org.tomfoolery.infrastructures.adapters.controllers.admin.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.usecases.admin.auth.UpdateStaffCredentialsUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.infrastructures.utils.helpers.adapters.UserIdAdapter;

public final class UpdateStaffCredentialsController implements ThrowableConsumer<UpdateStaffCredentialsController.RequestObject> {
    private final @NonNull UpdateStaffCredentialsUseCase updateStaffCredentialsUseCase;

    public static @NonNull UpdateStaffCredentialsController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull StaffRepository staffRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new UpdateStaffCredentialsController(authenticationTokenGenerator, authenticationTokenRepository, staffRepository, passwordEncoder);
    }

    private UpdateStaffCredentialsController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull StaffRepository staffRepository, @NonNull PasswordEncoder passwordEncoder) {
        this.updateStaffCredentialsUseCase = UpdateStaffCredentialsUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, staffRepository, passwordEncoder);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws UpdateStaffCredentialsUseCase.AuthenticationTokenNotFoundException, UpdateStaffCredentialsUseCase.AuthenticationTokenInvalidException, UpdateStaffCredentialsUseCase.StaffCredentialsInvalidException, UpdateStaffCredentialsUseCase.StaffNotFoundException {
        val requestModel = generateRequestModelFromRequestObject(requestObject);
        this.updateStaffCredentialsUseCase.accept(requestModel);
    }

    private static UpdateStaffCredentialsUseCase.@NonNull Request generateRequestModelFromRequestObject(@NonNull RequestObject requestObject) {
        val rawStaffId = requestObject.getStaffId();
        val staffId = UserIdAdapter.generateUserIdFromString(rawStaffId);

        val newStaffCredentials = requestObject.getNewStaffCredentials();

        return UpdateStaffCredentialsUseCase.Request.of(staffId, newStaffCredentials);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String staffId;
        Staff.@NonNull Credentials newStaffCredentials;
    }
}
