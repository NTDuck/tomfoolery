package org.tomfoolery.infrastructures.adapters.controllers.guest.auth.abc;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.usecases.users.authentication.LogUserInUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.guest.auth.LogUserInByCredentialsController;

import java.util.Map;

public abstract class LogUserInController {
    protected static final @NonNull Map<Class<? extends BaseUser>, LogUserInByCredentialsController.UserType> userTypesByUserClasses = Map.of(
        Administrator.class, UserType.ADMINISTRATOR,
        Patron.class, UserType.PATRON,
        Staff.class, UserType.STAFF
    );

    public enum UserType {
        ADMINISTRATOR, PATRON, STAFF,
    }

    @Value
    public static class ViewModel {
        @NonNull UserType userType;

        public static @NonNull ViewModel fromResponseModel(LogUserInUseCase.@NonNull Response responseModel) {
            val userClass = responseModel.getLoggedInUserClass();
            val userType = userTypesByUserClasses.get(userClass);

            return new ViewModel(userType);
        }
    }
}
