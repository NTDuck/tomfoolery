package org.tomfoolery.core.dataproviders.auth;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.abc.ReadonlyUser;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

import java.time.LocalDateTime;

public interface AuthenticationTokenService {
    @NonNull AuthenticationToken generateToken(ReadonlyUser.@NonNull Id userId, @NonNull Class<? extends ReadonlyUser> userClass, @NonNull LocalDateTime expiryTimestamp);
    void invalidateToken(@NonNull AuthenticationToken token);

    boolean verifyToken(@NonNull AuthenticationToken token);

    default boolean verifyToken(@NonNull AuthenticationToken token, @NonNull Class<? extends ReadonlyUser> userClass) {
        return userClass.equals(this.getUserClassFromToken(token)) && this.verifyToken(token);
    }

    ReadonlyUser.@NonNull Id getUserIdFromToken(@NonNull AuthenticationToken token);
    @NonNull Class<? extends ReadonlyUser> getUserClassFromToken(@NonNull AuthenticationToken token);
}
