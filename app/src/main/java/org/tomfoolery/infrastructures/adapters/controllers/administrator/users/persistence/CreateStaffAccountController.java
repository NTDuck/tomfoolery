package org.tomfoolery.infrastructures.adapters.controllers.administrator.users.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.usecases.administrator.users.persistence.CreateStaffAccountUseCase;
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
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        this.createStaffAccountUseCase.accept(requestModel);
    }

    private static CreateStaffAccountUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        val staffPassword = SecureString.of(requestObject.getStaffPassword());
        val staffCredentials = Staff.Credentials.of(requestObject.getStaffUsername(), staffPassword);

        return CreateStaffAccountUseCase.Request.of(staffCredentials);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String staffUsername;
        char @NonNull [] staffPassword;
    }
}
