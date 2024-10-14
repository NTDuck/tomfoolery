package org.tomfoolery.core.dataproviders.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.ReadonlyUser;

public interface PasswordEncoder {
    @NonNull String encode(@NonNull String password);
    
    default void encode(ReadonlyUser.@NonNull Credentials credentials) {
        val password = credentials.getPassword();
        val encodedPassword = this.encode(password);
        credentials.setPassword(encodedPassword);
    }

    boolean verify(@NonNull String password, @NonNull String encodedPassword);
}
