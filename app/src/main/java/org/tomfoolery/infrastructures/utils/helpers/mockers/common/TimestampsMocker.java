package org.tomfoolery.infrastructures.utils.helpers.mockers.common;

import com.github.javafaker.Faker;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(staticName = "of")
public class TimestampsMocker {
    private static final @Unsigned int DAYS_CREATED_WITHIN = 4444;

    private final @NonNull Faker faker = Faker.instance();

    public @NonNull Instant createMockTimestamps() {
        return this.faker.date()
            .past(DAYS_CREATED_WITHIN, TimeUnit.DAYS)
            .toInstant();
    }
}
