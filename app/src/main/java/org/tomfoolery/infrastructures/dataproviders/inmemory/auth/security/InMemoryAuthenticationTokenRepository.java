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
    public void save(@NonNull AuthenticationToken token) {
        this.authenticationToken = token;
    }

    @Override
    public void delete() {
        this.authenticationToken = null;
    }

    @Override
    public @Nullable AuthenticationToken get() {
        return this.authenticationToken;
    }
}