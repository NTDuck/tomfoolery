package org.tomfoolery.core.dataproviders.auth;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.ReadonlyUser;
import org.tomfoolery.core.domain.auth.AuthenticationToken;

public interface AuthenticationTokenService {
    <User extends ReadonlyUser> @NonNull AuthenticationToken generateToken(User.@NonNull Id userId);
    void invalidateToken(@NonNull AuthenticationToken token);

    boolean verifyToken(@NonNull AuthenticationToken token);

    default <User extends ReadonlyUser> boolean verifyToken(@NonNull AuthenticationToken token, @NonNull Class<User> userClass) {
        return userClass.equals(this.getUserClassFromToken(token)) && this.verifyToken(token);
    }

    <User extends ReadonlyUser> User.@NonNull Id getUserIdFromToken(@NonNull AuthenticationToken token);
    <User extends ReadonlyUser> @NonNull Class<User> getUserClassFromToken(@NonNull AuthenticationToken token);
}
