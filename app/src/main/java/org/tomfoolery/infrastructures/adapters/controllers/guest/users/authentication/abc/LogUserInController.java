package org.tomfoolery.infrastructures.adapters.controllers.guest.users.authentication.abc;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.usecases.guest.users.authentication.abc.LogUserInUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.guest.users.authentication.LogUserInByCredentialsController;

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

    protected static @NonNull ViewModel mapResponseModelToViewModel(LogUserInUseCase.@NonNull Response responseModel) {
        val username = responseModel.getLoggedInUsername();
        val userClass = responseModel.getLoggedInUserClass();
        val userType = userTypesByUserClasses.get(userClass);

        return ViewModel.of(username, userType);
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull String loggedInUsername;
        @NonNull UserType loggedInUserType;
    }
}
