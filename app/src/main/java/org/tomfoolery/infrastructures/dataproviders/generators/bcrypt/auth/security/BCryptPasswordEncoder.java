package org.tomfoolery.infrastructures.dataproviders.generators.bcrypt.auth.security;

import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;

@NoArgsConstructor(staticName = "of")
public class BCryptPasswordEncoder implements PasswordEncoder {
    private static final BCrypt.@NonNull Hasher encoder = BCrypt.withDefaults();
    private static final BCrypt.@NonNull Verifyer verifyer = BCrypt.verifyer();

    private static final int COST = 12;

    @Override
    public @NonNull SecureString encode(@NonNull SecureString rawPassword) {
        val rawPasswordChars = rawPassword.getChars();
        val encodedChars = encoder.hashToChar(COST, rawPasswordChars);

        return SecureString.of(encodedChars);
    }

    @Override
    public boolean verify(@NonNull SecureString rawPassword, @NonNull SecureString encodedPassword) {
        val rawPasswordChars = rawPassword.getChars();

        return verifyer.verify(rawPasswordChars, encodedPassword).verified;
    }
}
