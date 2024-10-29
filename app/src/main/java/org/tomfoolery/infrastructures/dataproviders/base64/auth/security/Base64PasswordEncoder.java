package org.tomfoolery.infrastructures.dataproviders.base64.auth.security;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.security.PasswordEncoder;
import org.tomfoolery.infrastructures.utils.helpers.Base64Encoder;

@NoArgsConstructor(staticName = "of")
public class Base64PasswordEncoder implements PasswordEncoder {
    @Override
    @SneakyThrows
    public @NonNull String encode(@NonNull String password) {
        return Base64Encoder.encode(password);
    }

    @Override
    @SneakyThrows
    public boolean verify(@NonNull String password, @NonNull String encodedPassword) {
        return this.encode(password).equals(encodedPassword);
    }
}
