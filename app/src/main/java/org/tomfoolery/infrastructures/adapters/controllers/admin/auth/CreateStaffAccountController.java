package org.tomfoolery.infrastructures.adapters.controllers.admin.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.usecases.users.account.staff.persistence.CreateStaffAccountUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;

public final class CreateStaffAccountController implements ThrowableConsumer<CreateStaffAccountController.RequestObject> {
    private final @NonNull CreateStaffAccountUseCase createStaffAccountUseCase;

    public static @NonNull CreateStaffAccountController of(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new CreateStaffAccountController(staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private CreateStaffAccountController(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        this.createStaffAccountUseCase = CreateStaffAccountUseCase.of(staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    @Override
    public void accept(CreateStaffAccountController.@NonNull RequestObject requestObject) throws CreateStaffAccountUseCase.AuthenticationTokenNotFoundException, CreateStaffAccountUseCase.AuthenticationTokenInvalidException, CreateStaffAccountUseCase.StaffCredentialsInvalidException, CreateStaffAccountUseCase.StaffAlreadyExistsException {
        val requestModel = requestObject.toRequestModel();
        this.createStaffAccountUseCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String staffUsername;
        char @NonNull [] staffPassword;

        private CreateStaffAccountUseCase.@NonNull Request toRequestModel() {
            val staffPassword = SecureString.of(this.staffPassword);
            val staffCredentials = Staff.Credentials.of(staffUsername, staffPassword);

            return CreateStaffAccountUseCase.Request.of(staffCredentials);
        }
    }
}
