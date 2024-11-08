package org.tomfoolery.infrastructures.adapters.controllers.admin.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.usecases.admin.auth.UpdateStaffCredentialsUseCase;
import org.tomfoolery.infrastructures.utils.contracts.Controller;
import org.tomfoolery.infrastructures.utils.dataclasses.ViewableUserId;

public class UpdateStaffCredentialsController implements Controller<UpdateStaffCredentialsController.RequestObject, UpdateStaffCredentialsUseCase.Request> {
    @Override
    public UpdateStaffCredentialsUseCase.@NonNull Request apply(@NonNull RequestObject requestObject) {
        val viewableStaffId = requestObject.getStaffId();
        val staffId = viewableStaffId.toUserId();

        val newStaffCredentials = requestObject.getNewStaffCredentials();

        return UpdateStaffCredentialsUseCase.Request.of(staffId, newStaffCredentials);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull ViewableUserId staffId;
        Staff.@NonNull Credentials newStaffCredentials;
    }
}
