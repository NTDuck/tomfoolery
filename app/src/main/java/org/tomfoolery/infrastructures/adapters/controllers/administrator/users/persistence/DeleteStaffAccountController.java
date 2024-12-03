package org.tomfoolery.infrastructures.adapters.controllers.administrator.users.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.usecases.administrator.users.persistence.DeleteStaffAccountUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

import java.util.UUID;

public final class DeleteStaffAccountController implements ThrowableConsumer<DeleteStaffAccountController.RequestObject> {
    private final @NonNull DeleteStaffAccountUseCase deleteStaffAccountUseCase;

    public static @NonNull DeleteStaffAccountController of(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new DeleteStaffAccountController(staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private DeleteStaffAccountController(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.deleteStaffAccountUseCase = DeleteStaffAccountUseCase.of(staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws DeleteStaffAccountUseCase.AuthenticationTokenNotFoundException, DeleteStaffAccountUseCase.AuthenticationTokenInvalidException, DeleteStaffAccountUseCase.StaffNotFoundException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        this.deleteStaffAccountUseCase.accept(requestModel);
    }

    private static DeleteStaffAccountUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        val staffId = Staff.Id.of(UUID.fromString(requestObject.getStaffId()));

        return DeleteStaffAccountUseCase.Request.of(staffId);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String staffId;
    }
}
