package org.tomfoolery.core.utils.helpers.verifiers.users.patron;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class DateOfBirthVerifier {
    private static final @Unsigned int MIN_AGE = 0;
    private static final @Unsigned int MAX_AGE = 144;

    public static boolean verify(@NonNull LocalDate dateOfBirth) {
        return !dateOfBirth.isBefore(LocalDate.now().minusYears(MAX_AGE))
            && !dateOfBirth.isAfter(LocalDate.now().minusYears(MIN_AGE));
    }
}
