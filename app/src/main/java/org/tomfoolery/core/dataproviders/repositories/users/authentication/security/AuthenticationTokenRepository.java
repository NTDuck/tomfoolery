package org.tomfoolery.core.dataproviders.repositories.users.authentication.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.AuthenticationToken;

public interface AuthenticationTokenRepository {
    void saveAuthenticationToken(@NonNull AuthenticationToken authenticationToken);
    void removeAuthenticationToken();
    
    @Nullable AuthenticationToken getAuthenticationToken();

    default boolean containsAuthenticationToken() {
        return this.getAuthenticationToken() != null;
    }
}
