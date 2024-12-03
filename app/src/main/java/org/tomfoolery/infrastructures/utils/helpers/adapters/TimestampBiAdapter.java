package org.tomfoolery.infrastructures.utils.helpers.adapters;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class TimestampBiAdapter {
    private static final DateTimeFormatter PARSING_FORMATTER = DateTimeFormatter.ISO_INSTANT;
    private static final DateTimeFormatter SERIALIZATION_FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME;

    public static @NonNull Instant parse(@NonNull String rawTimestamp) throws TimestampInvalidException {
        try {
            val temporalAccessor = PARSING_FORMATTER.parse(rawTimestamp);
            return Instant.from(temporalAccessor);

        } catch (DateTimeException exception) {
            throw new TimestampInvalidException();
        }
    }

    public static @NonNull String serialize(@NonNull Instant timestamp) {
        return SERIALIZATION_FORMATTER.format(timestamp);
    }

    public static class TimestampInvalidException extends Exception {}
}
