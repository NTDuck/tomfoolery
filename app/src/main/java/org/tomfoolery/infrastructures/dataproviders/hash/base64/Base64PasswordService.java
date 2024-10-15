package org.tomfoolery.infrastructures.dataproviders.hash.base64;

import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.PasswordService;
import org.tomfoolery.infrastructures.utils.services.Base64Service;

@NoArgsConstructor(staticName = "of")
public class Base64PasswordService implements PasswordService {
    @Override
    public @NonNull String encodePassword(@NonNull String password) {
        return Base64Service.convertNormalStringToBase64String(password);
    }

    @Override
    public boolean verifyPassword(@NonNull String password, @NonNull String encodedPassword) {
        return this.encodePassword(password).equals(encodedPassword);
    }
}
