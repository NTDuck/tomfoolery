package org.tomfoolery.core.utils.helpers.verifiers.users.patron;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.utils.helpers.verifiers.StringVerifier;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class AddressVerifier {
    public static boolean verify(Patron.Metadata.@NonNull Address address) {
        return StringVerifier.verify(address.getCity())
            && StringVerifier.verify(address.getCountry());
    }
}
