package org.tomfoolery.core.utils.helpers.verifiers.documents;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ISBNVerifier {
    /**
     * @see <a href="https://en.wikipedia.org/wiki/ISBN#ISBN-10_check_digits">ISBN-10 Specification</a>
     */
    public static boolean verifyISBN10(@NonNull String ISBN10) {
        if (ISBN10.length() != 10)
            return false;

        val expectedCheckDigit = calculateISBN10CheckDigit(ISBN10.substring(0, 9));
        val actualCheckDigit = ISBN10.charAt(9);

        return expectedCheckDigit == actualCheckDigit;
    }

    /**
     * @see <a href="https://en.wikipedia.org/wiki/ISBN#ISBN-13_check_digit_calculation">ISBN-13 Specification</a>
     */
    public static boolean verifyISBN13(@NonNull String ISBN13) {
        if (ISBN13.length() != 13)
            return false;

        val expectedCheckDigit = calculateISBN13CheckDigit(ISBN13.substring(0, 12));
        val actualCheckDigit = ISBN13.charAt(12);

        return expectedCheckDigit == actualCheckDigit;
    }

    /**
     * @see <a href="https://en.wikipedia.org/wiki/ISBN#ISBN-10_check_digit_calculation">ISBN-10 Specification</a>
     */
    public static @Unsigned char calculateISBN10CheckDigit(@NonNull String ISBN10WithoutCheckDigit) {
        if (ISBN10WithoutCheckDigit.length() != 9)
            throw new IllegalArgumentException(String.format("Invalid length of ISBN-10 (without check digit) '%s': %d", ISBN10WithoutCheckDigit, ISBN10WithoutCheckDigit.length()));

        var weightedSum = calculateISBN10WeightedSum(ISBN10WithoutCheckDigit);
        val checkDigit = 11 - weightedSum % 11;

        return checkDigit == 10 ? 'X' : Character.forDigit(checkDigit, 10);
    }
    /**
     * @see <a href="https://en.wikipedia.org/wiki/ISBN#ISBN-13_check_digit_calculation">ISBN-13 Specification</a>
     */
    public static @Unsigned char calculateISBN13CheckDigit(@NonNull String ISBN13WithoutCheckDigit) {
        if (ISBN13WithoutCheckDigit.length() != 12)
            throw new IllegalArgumentException(String.format("Invalid length of ISBN-13 (without check digit) '%s': %d", ISBN13WithoutCheckDigit, ISBN13WithoutCheckDigit.length()));

        val weightedSum = calculateISBN13WeightedSum(ISBN13WithoutCheckDigit);
        val checkDigit = 10 - weightedSum % 10;

        return Character.forDigit(checkDigit, 10);
    }

    private static @Unsigned int calculateISBN10WeightedSum(@NonNull String ISBN10WithoutCheckDigit) {
        var weightedSum = 0;

        for (var index = 0; index < 9; ++index) {
            val rawDigit = ISBN10WithoutCheckDigit.charAt(index);

            if (!Character.isDigit(rawDigit))
                throw new IllegalArgumentException(String.format("Invalid digit of ISBN-10 '%s' at index %d: '%c'", ISBN10WithoutCheckDigit, index, rawDigit));

            val digit = Character.digit(rawDigit, 10);
            val weight = 10 - index;

            weightedSum += digit * weight;
        }

        return weightedSum;
    }

    private static @Unsigned int calculateISBN13WeightedSum(@NonNull String ISBN13WithoutCheckDigit) {
        var weightedSum = 0;

        for (var index = 0; index < 12; ++index) {
            val rawDigit = ISBN13WithoutCheckDigit.charAt(index);

            if (!Character.isDigit(rawDigit))
                throw new IllegalArgumentException(String.format("Invalid digit of ISBN-13 '%s' at index %d: '%c'", ISBN13WithoutCheckDigit, index, rawDigit));

            val digit = Character.digit(rawDigit, 10);
            val weight = (index % 2 == 0) ? 1 : 3;

            weightedSum += digit * weight;
        }

        return weightedSum;
    }
}
