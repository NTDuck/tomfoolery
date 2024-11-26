package org.tomfoolery.core.utils.dataclasses.documents;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;

@Data
@Setter(value = AccessLevel.NONE)
public class AverageRating {
    public static final @Unsigned double MIN_RATING_VALUE = 0;
    public static final @Unsigned double MAX_RATING_VALUE = 5;

    private @Unsigned double value;
    private @Unsigned int numberOfRatings = 0;

    public static @NonNull AverageRating of() {
        return new AverageRating(MIN_RATING_VALUE);
    }

    public static @NonNull AverageRating of(@Unsigned double value) {
        return new AverageRating(value);
    }

    private AverageRating(@Unsigned double value) {
        this.value = value;
    }

    public static boolean isValid(@Unsigned double value) {
        return value >= MIN_RATING_VALUE
            && value <= MAX_RATING_VALUE;
    }

    public void addRating(@Unsigned double value) {
        this.value = (this.value * this.numberOfRatings + value) / (this.numberOfRatings + 1);
        ++this.numberOfRatings;
    }

    public void removeRating(@Unsigned double value) {
        if (this.numberOfRatings <= 1) {
            this.value = MIN_RATING_VALUE;
            this.numberOfRatings = 0;

            return;
        }

        this.value = (this.value * this.numberOfRatings - value) / (this.numberOfRatings - 1);
        --this.numberOfRatings;
    }
}
