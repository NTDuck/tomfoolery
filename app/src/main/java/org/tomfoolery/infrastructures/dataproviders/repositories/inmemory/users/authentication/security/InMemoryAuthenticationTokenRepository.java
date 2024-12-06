package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.authentication.security;

import lombok.Locked;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.utils.dataclasses.auth.security.AuthenticationToken;

@NoArgsConstructor(staticName = "of")
public class InMemoryAuthenticationTokenRepository implements AuthenticationTokenRepository {
    private @Nullable AuthenticationToken authenticationToken = null;

    @Override
    @Locked.Write
    public void saveAuthenticationToken(@NonNull AuthenticationToken authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    @Override
    @Locked.Write
    public void removeAuthenticationToken() {
        this.authenticationToken = null;
    }

    @Override
    @Locked.Read
    public @Nullable AuthenticationToken getAuthenticationToken() {
        return this.authenticationToken;
    }
}