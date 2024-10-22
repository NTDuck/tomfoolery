package org.tomfoolery.infrastructures.dataproviders.inmemory;

import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepository;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

@NoArgsConstructor(staticName = "of")
public class InMemoryAuthenticationTokenRepository implements AuthenticationTokenRepository {
    private @Nullable AuthenticationToken authenticationToken = null;

    @Override
    public void saveToken(@NonNull AuthenticationToken token) {
        this.authenticationToken = token;
    }

    @Override
    public void removeToken() {
        this.authenticationToken = null;
    }

    @Override
    public @Nullable AuthenticationToken getToken() {
        return this.authenticationToken;
    }
}