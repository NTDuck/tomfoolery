package org.tomfoolery.infrastructures.adapters.controllers.admin.auth;

import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.usecases.admin.auth.DeleteStaffAccountUseCase;
import org.tomfoolery.infrastructures.utils.contracts.Controller;
import org.tomfoolery.infrastructures.utils.dataclasses.ViewableUserId;

@NoArgsConstructor(staticName = "of")
public class DeleteStaffAccountController implements Controller<DeleteStaffAccountController.RequestObject, DeleteStaffAccountUseCase.Request> {
    @Override
    public DeleteStaffAccountUseCase.@NonNull Request apply(@NonNull RequestObject requestObject) {
        val viewableStaffId = requestObject.getStaffId();
        val staffId = viewableStaffId.toUserId();

        return DeleteStaffAccountUseCase.Request.of(staffId);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull ViewableUserId staffId;
    }
}
