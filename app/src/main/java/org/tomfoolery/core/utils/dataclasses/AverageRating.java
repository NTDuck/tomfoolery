package org.tomfoolery.core.utils.dataclasses;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data(staticConstructor = "of")
public class AverageRating {
    public static final double MIN_RATING_VALUE = 0;
    public static final double MAX_RATING_VALUE = 5;

    private double ratingValue = MIN_RATING_VALUE;

    @Setter(value = AccessLevel.NONE)
    private int ratingCount = 0;

    public static boolean isRatingValueValid(double ratingValue) {
        return ratingValue >= MIN_RATING_VALUE
            && ratingValue <= MAX_RATING_VALUE;
    }

    public void addRating(double ratingValue) {
        this.ratingValue = (this.ratingValue * this.ratingCount + ratingValue) / (this.ratingCount + 1);
        ++this.ratingCount;
    }

    public void removeRating(double ratingValue) {
        if (this.ratingCount <= 1) {
            this.ratingValue = MIN_RATING_VALUE;
            this.ratingCount = 0;

            return;
        }

        this.ratingValue = (this.ratingValue * this.ratingCount - ratingValue) / (this.ratingCount - 1);
        --this.ratingCount;
    }
}
