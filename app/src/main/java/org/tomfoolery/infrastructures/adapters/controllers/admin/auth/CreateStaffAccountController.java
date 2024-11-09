package org.tomfoolery.infrastructures.adapters.controllers.admin.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.usecases.admin.auth.CreateStaffAccountUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

public final class CreateStaffAccountController implements ThrowableConsumer<CreateStaffAccountController.RequestObject> {
    private final @NonNull CreateStaffAccountUseCase createStaffAccountUseCase;

    public static @NonNull CreateStaffAccountController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull StaffRepository staffRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new CreateStaffAccountController(authenticationTokenGenerator, authenticationTokenRepository, staffRepository, passwordEncoder);
    }

    private CreateStaffAccountController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull StaffRepository staffRepository, @NonNull PasswordEncoder passwordEncoder) {
        this.createStaffAccountUseCase = CreateStaffAccountUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, staffRepository, passwordEncoder);
    }

    @Override
    public void accept(CreateStaffAccountController.@NonNull RequestObject requestObject) throws CreateStaffAccountUseCase.AuthenticationTokenNotFoundException, CreateStaffAccountUseCase.AuthenticationTokenInvalidException, CreateStaffAccountUseCase.StaffCredentialsInvalidException, CreateStaffAccountUseCase.StaffAlreadyExistsException {
        val requestModel = requestObject.toRequestModel();
        this.createStaffAccountUseCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String staffUsername;
        @NonNull String staffPassword;

        private CreateStaffAccountUseCase.@NonNull Request toRequestModel() {
            val staffCredentials = Staff.Credentials.of(staffUsername, staffPassword);

            return CreateStaffAccountUseCase.Request.of(staffCredentials);
        }
    }
}
