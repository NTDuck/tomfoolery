package org.tomfoolery.infrastructures.dataproviders.inmemory.auth.security;

import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

@NoArgsConstructor(staticName = "of")
public class InMemoryAuthenticationTokenRepository implements AuthenticationTokenRepository {
    private @Nullable AuthenticationToken authenticationToken = null;

    @Override
    public void saveAuthenticationToken(@NonNull AuthenticationToken authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    @Override
    public void deleteAuthenticationToken() {
        this.authenticationToken = null;
    }

    @Override
    public @Nullable AuthenticationToken getAuthenticationToken() {
        return this.authenticationToken;
    }
}