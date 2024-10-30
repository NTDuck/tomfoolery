package org.tomfoolery.infrastructures.dataproviders.hash.base64;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.PasswordService;
import org.tomfoolery.infrastructures.utils.services.Base64Service;

@NoArgsConstructor(staticName = "of")
public class Base64PasswordService implements PasswordService {
    @Override
    @SneakyThrows
    public @NonNull String encodePassword(@NonNull String password) {
        return Base64Service.serialize(password);
    }

    @Override
    @SneakyThrows
    public boolean verifyPassword(@NonNull String password, @NonNull String encodedPassword) {
        return this.encodePassword(password).equals(encodedPassword);
    }
}
