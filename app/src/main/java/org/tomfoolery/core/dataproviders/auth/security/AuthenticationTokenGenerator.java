package org.tomfoolery.core.dataproviders.auth.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

import java.time.LocalDateTime;

public interface AuthenticationTokenGenerator {
    @NonNull AuthenticationToken generateToken(BaseUser.@NonNull Id userId, @NonNull Class<? extends BaseUser> userClass, @NonNull LocalDateTime expiryTimestamp);
    void invalidateToken(@NonNull AuthenticationToken token);

    boolean verifyToken(@NonNull AuthenticationToken token);

    default boolean verifyToken(@NonNull AuthenticationToken token, @NonNull Class<? extends BaseUser> userClass) {
        return this.verifyToken(token)
            && userClass.equals(this.getUserClassFromToken(token));
    }

    BaseUser.@Nullable Id getUserIdFromToken(@NonNull AuthenticationToken token);
    @Nullable Class<? extends BaseUser> getUserClassFromToken(@NonNull AuthenticationToken token);
}
