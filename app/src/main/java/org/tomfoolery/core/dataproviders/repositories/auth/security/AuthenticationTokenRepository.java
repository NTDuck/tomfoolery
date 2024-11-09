package org.tomfoolery.core.dataproviders.repositories.auth.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

public interface AuthenticationTokenRepository {
    void saveAuthenticationToken(@NonNull AuthenticationToken authenticationToken);
    void deleteAuthenticationToken();
    
    @Nullable AuthenticationToken getAuthenticationToken();

    default boolean containsAuthenticationToken() {
        return this.getAuthenticationToken() != null;
    }
}
