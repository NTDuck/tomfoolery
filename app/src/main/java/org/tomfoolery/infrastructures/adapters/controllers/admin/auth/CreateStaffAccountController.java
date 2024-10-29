package org.tomfoolery.infrastructures.adapters.controllers.admin.auth;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.StaffRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.auth.security.PasswordEncoder;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.usecases.external.admin.auth.CreateStaffAccountUseCase;
import org.tomfoolery.infrastructures.utils.contracts.ThrowableConsumerController;

@RequiredArgsConstructor(staticName = "of")
public class CreateStaffAccountController implements ThrowableConsumerController<CreateStaffAccountController.RequestObject, CreateStaffAccountUseCase.Request> {
    private final @NonNull CreateStaffAccountUseCase useCase;

    private CreateStaffAccountController(@NonNull StaffRepository staffRepository, @NonNull PasswordEncoder passwordEncoder, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.useCase = CreateStaffAccountUseCase.of(staffRepository, passwordEncoder, authenticationTokenGenerator, authenticationTokenRepository);
    }

    public static @NonNull CreateStaffAccountController of(@NonNull StaffRepository staffRepository, @NonNull PasswordEncoder passwordEncoder, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new CreateStaffAccountController(staffRepository, passwordEncoder, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public CreateStaffAccountUseCase.@NonNull Request getRequestModelFromRequestObject(@NonNull RequestObject requestObject) {
        val staffUsername = requestObject.getUsername();
        val staffPassword = requestObject.getPassword();
        val staffCredentials = Staff.Credentials.of(staffUsername, staffPassword);

        return CreateStaffAccountUseCase.Request.of(staffCredentials);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws CreateStaffAccountUseCase.AdminAuthenticationTokenNotFoundException, CreateStaffAccountUseCase.AdminAuthenticationTokenInvalidException, CreateStaffAccountUseCase.StaffCredentialsInvalidException, CreateStaffAccountUseCase.StaffAlreadyExistsException {
        val requestModel = this.getRequestModelFromRequestObject(requestObject);
        this.useCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String username;
        @NonNull String password;
    }
}
