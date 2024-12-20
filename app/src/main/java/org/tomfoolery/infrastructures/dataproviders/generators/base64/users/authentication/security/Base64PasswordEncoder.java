package org.tomfoolery.infrastructures.dataproviders.generators.base64.users.authentication.security;

import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;
import org.tomfoolery.infrastructures.utils.helpers.base64.Base64Codec;

@NoArgsConstructor(staticName = "of")
public class Base64PasswordEncoder implements PasswordEncoder {
    @Override
    public @NonNull SecureString encode(@NonNull SecureString rawPassword) {
        val encodedPasswordCharSequence = Base64Codec.encode(rawPassword);
        return SecureString.of(encodedPasswordCharSequence);
    }
}
