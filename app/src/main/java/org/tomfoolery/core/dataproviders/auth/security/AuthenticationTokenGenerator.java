package org.tomfoolery.core.dataproviders.auth.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.domain.auth.abc.ReadonlyUser;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

import java.time.LocalDateTime;

public interface AuthenticationTokenGenerator {
    @NonNull AuthenticationToken generateToken(ReadonlyUser.@NonNull Id userId, @NonNull Class<? extends ReadonlyUser> userClass, @NonNull LocalDateTime expiryTimestamp);
    void invalidateToken(@NonNull AuthenticationToken token);

    boolean verifyToken(@NonNull AuthenticationToken token);

    default boolean verifyToken(@NonNull AuthenticationToken token, @NonNull Class<? extends ReadonlyUser> userClass) {
        return this.verifyToken(token)
            && userClass.equals(this.getUserClassFromToken(token));
    }

    ReadonlyUser.@Nullable Id getUserIdFromToken(@NonNull AuthenticationToken token);
    @Nullable Class<? extends ReadonlyUser> getUserClassFromToken(@NonNull AuthenticationToken token);
}
