package org.tomfoolery.core.dataproviders.auth;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.domain.abc.ReadonlyUser;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

import java.time.LocalDateTime;

public interface AuthenticationTokenService {
    <User extends ReadonlyUser> @NonNull AuthenticationToken generateToken(User.@NonNull Id userId, @NonNull Class<User> userClass, @NonNull LocalDateTime expiryTimestamp);
    void invalidateToken(@NonNull AuthenticationToken token);

    boolean verifyToken(@NonNull AuthenticationToken token);

    default <User extends ReadonlyUser> boolean verifyToken(@NonNull AuthenticationToken token, @NonNull Class<User> userClass) {
        return userClass.equals(this.getUserClassFromToken(token)) && this.verifyToken(token);
    }

    <User extends ReadonlyUser> User.@Nullable Id getUserIdFromToken(@NonNull AuthenticationToken token);
    <User extends ReadonlyUser> @Nullable Class<User> getUserClassFromToken(@NonNull AuthenticationToken token);
}
