package org.tomfoolery.core.utils.helpers.verifiers.auth.patron;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;

import java.time.Duration;
import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class DateOfBirthVerifier {
    private static final @Unsigned double DAYS_PER_YEAR = 365.25;

    private static final @Unsigned int MIN_AGE = 0;
    private static final @Unsigned int MAX_AGE = 144;

    public static boolean verify(@NonNull LocalDate dateOfBirth) {
        return dateOfBirth.isAfter(calculateTimestamp(MAX_AGE))
            && dateOfBirth.isBefore(calculateTimestamp(MIN_AGE));
    }

    private static @NonNull LocalDate calculateTimestamp(@Unsigned int yearsFromNow) {
        val daysFromNow = DAYS_PER_YEAR * yearsFromNow;

        return LocalDate.now()
            .minus(Duration.ofDays((long) daysFromNow));
    }
}
