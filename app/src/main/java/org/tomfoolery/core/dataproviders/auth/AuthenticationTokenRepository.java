package org.tomfoolery.core.dataproviders.auth;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

public interface AuthenticationTokenRepository {
    void saveToken(@NonNull AuthenticationToken token);
    void removeToken();
    
    @Nullable AuthenticationToken getToken();

    default boolean containsToken() {
        return this.getToken() != null;
    }
}
