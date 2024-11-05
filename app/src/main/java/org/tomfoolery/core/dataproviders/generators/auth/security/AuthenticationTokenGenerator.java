package org.tomfoolery.core.dataproviders.generators.auth.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

import java.time.Instant;

public interface AuthenticationTokenGenerator {
    @NonNull AuthenticationToken generateAuthenticationToken(BaseUser.@NonNull Id userId, @NonNull Class<? extends BaseUser> userClass, @NonNull Instant expiryTimestamp);

    void invalidateAuthenticationToken(@NonNull AuthenticationToken authenticationToken);
    boolean verifyAuthenticationToken(@NonNull AuthenticationToken authenticationToken);

    BaseUser.@Nullable Id getUserIdFromAuthenticationToken(@NonNull AuthenticationToken authenticationToken);
    @Nullable Class<? extends BaseUser> getUserClassFromAuthenticationToken(@NonNull AuthenticationToken authenticationToken);
}
