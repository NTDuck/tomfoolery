package org.tomfoolery.core.dataproviders.auth.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

public interface AuthenticationTokenRepository {
    void save(@NonNull AuthenticationToken token);
    void delete();
    
    @Nullable AuthenticationToken get();

    default boolean contains() {
        return this.get() != null;
    }
}
