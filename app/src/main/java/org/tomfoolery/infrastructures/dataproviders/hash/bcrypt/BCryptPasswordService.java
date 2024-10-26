package org.tomfoolery.infrastructures.dataproviders.hash.bcrypt;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.PasswordService;

public class BCryptPasswordService implements PasswordService {
    private static final BCrypt.@NonNull Hasher encoder = BCrypt.withDefaults();
    private static final BCrypt.@NonNull Verifyer verifyer = BCrypt.verifyer();
    private static final int COST = 12;

    @Override
    public @NonNull String encodePassword(@NonNull String password) {
        return encoder.hashToString(COST, password.toCharArray());
    }

    @Override
    public boolean verifyPassword(@NonNull String password, @NonNull String encodedPassword) {
        return verifyer.verify(password.toCharArray(), encodedPassword).verified;
    }
}
