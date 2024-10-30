package org.tomfoolery.infrastructures.adapters.controllers.admin.auth;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.StaffRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.dataproviders.auth.PasswordService;
import org.tomfoolery.core.domain.Staff;
import org.tomfoolery.core.usecases.external.admin.CreateStaffAccountUseCase;
import org.tomfoolery.infrastructures.utils.contracts.ThrowableConsumerController;

@RequiredArgsConstructor(staticName = "of")
public class CreateStaffAccountController implements ThrowableConsumerController<CreateStaffAccountController.RequestObject, CreateStaffAccountUseCase.Request> {
    private final @NonNull CreateStaffAccountUseCase useCase;

    private CreateStaffAccountController(@NonNull StaffRepository staffRepository, @NonNull PasswordService passwordService, @NonNull AuthenticationTokenService authenticationTokenService, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.useCase = CreateStaffAccountUseCase.of(staffRepository, passwordService, authenticationTokenService, authenticationTokenRepository);
    }

    public static @NonNull CreateStaffAccountController of(@NonNull StaffRepository staffRepository, @NonNull PasswordService passwordService, @NonNull AuthenticationTokenService authenticationTokenService, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new CreateStaffAccountController(staffRepository, passwordService, authenticationTokenService, authenticationTokenRepository);
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
