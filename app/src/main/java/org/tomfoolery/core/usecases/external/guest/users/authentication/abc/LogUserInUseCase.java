package org.tomfoolery.core.usecases.external.guest.users.authentication.abc;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.users.abc.BaseUser;

import java.time.Instant;

public abstract class LogUserInUseCase {
    protected <User extends BaseUser> void markUserAsLoggedIn(@NonNull User user) {
        val userAuditTimestamps = user.getAudit().getTimestamps();
        userAuditTimestamps.setLastLogin(Instant.now());
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull String loggedInUsername;
        @NonNull Class<? extends BaseUser> loggedInUserClass;
    }

    public static class UserNotFoundException extends Exception {}
}
