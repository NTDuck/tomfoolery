package org.tomfoolery.infrastructures.dataproviders.generators.base64.auth.security;

import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;
import org.tomfoolery.infrastructures.utils.helpers.base64.Base64Encoder;

import java.nio.CharBuffer;

@NoArgsConstructor(staticName = "of")
public class Base64PasswordEncoder implements PasswordEncoder {
    @Override
    public @NonNull SecureString encodePassword(@NonNull SecureString rawPassword) {
        val encodedPasswordCharSequence = Base64Encoder.encode(rawPassword);
        val encodedPasswordChars = CharBuffer.wrap(encodedPasswordCharSequence).array();

        return SecureString.of(encodedPasswordChars);
    }
}
