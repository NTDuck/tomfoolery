package org.tomfoolery.core.dataproviders.generators.users.authentication.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.generators.abc.BaseGenerator;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.AuthenticationToken;

import java.time.Instant;

public interface AuthenticationTokenGenerator extends BaseGenerator {
    @NonNull AuthenticationToken generate(BaseUser.@NonNull Id userId, @NonNull Class<? extends BaseUser> userClass, @NonNull Instant expiryTimestamp);

    void invalidate(@NonNull AuthenticationToken authenticationToken);
    boolean verify(@NonNull AuthenticationToken authenticationToken);

    BaseUser.@Nullable Id getUserIdFromAuthenticationToken(@NonNull AuthenticationToken authenticationToken);
    @Nullable Class<? extends BaseUser> getUserClassFromAuthenticationToken(@NonNull AuthenticationToken authenticationToken);
}
