package org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.administrator.users.persistence.DeleteStaffAccountUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.infrastructures.utils.helpers.adapters.UserIdBiAdapter;

public final class DeleteStaffAccountController implements ThrowableConsumer<DeleteStaffAccountController.RequestObject> {
    private final @NonNull DeleteStaffAccountUseCase deleteStaffAccountUseCase;

    public static @NonNull DeleteStaffAccountController of(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new DeleteStaffAccountController(staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private DeleteStaffAccountController(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.deleteStaffAccountUseCase = DeleteStaffAccountUseCase.of(staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws UserIdBiAdapter.UserUuidInvalidException, DeleteStaffAccountUseCase.AuthenticationTokenNotFoundException, DeleteStaffAccountUseCase.AuthenticationTokenInvalidException, DeleteStaffAccountUseCase.StaffNotFoundException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        this.deleteStaffAccountUseCase.accept(requestModel);
    }

    private static DeleteStaffAccountUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) throws UserIdBiAdapter.UserUuidInvalidException {
        val staffId = UserIdBiAdapter.parse(requestObject.getStaffUuid());

        return DeleteStaffAccountUseCase.Request.of(staffId);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String staffUuid;
    }
}
