package org.tomfoolery.core.dataproviders.repositories.users.authentication.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.AuthenticationToken;

public interface AuthenticationTokenRepository {
    void save(@NonNull AuthenticationToken authenticationToken);
    void remove();
    
    @Nullable AuthenticationToken get();

    default boolean contains() {
        return this.get() != null;
    }
}
