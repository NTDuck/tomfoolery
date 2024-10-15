package org.tomfoolery.core.dataproviders.auth;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.ReadonlyUser;
import org.tomfoolery.core.domain.auth.AuthenticationToken;

public interface AuthenticationTokenGenerator {
    <User extends ReadonlyUser> @NonNull AuthenticationToken generateToken(User.@NonNull Id userId);
    void invalidateToken(@NonNull AuthenticationToken token);

    boolean verifyToken(@NonNull AuthenticationToken token);
    <User extends ReadonlyUser> boolean verifyTokenForExactUserType(@NonNull AuthenticationToken token);

    <User extends ReadonlyUser> User.@NonNull Id getUserIdFromToken(@NonNull AuthenticationToken token);
}
