package org.tomfoolery.infrastructures.adapters.controllers.admin.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.usecases.admin.auth.DeleteStaffAccountUseCase;
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
        val requestModel = requestObject.toRequestModel();
        this.deleteStaffAccountUseCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String staffId;

        private DeleteStaffAccountUseCase.@NonNull Request toRequestModel() {
            val staffId = Staff.Id.of(UUID.fromString(this.staffId));

            return DeleteStaffAccountUseCase.Request.of(staffId);
        }
    }
}
