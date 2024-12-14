package org.tomfoolery.core.utils.helpers.verifiers.documents;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
@NoArgsConstructor(access = AccessLevel.NONE)
public final class ISBNVerifier {
    private static final int ISBN_10_LENGTH = 10;
    private static final int ISBN_13_LENGTH = 13;
    private static final int ISBN_10_PREFIX_LENGTH = ISBN_10_LENGTH - 1;
    private static final int ISBN_13_PREFIX_LENGTH = ISBN_13_LENGTH - 1;

    /**
     * @see <a href="https://en.wikipedia.org/wiki/ISBN#ISBN-10_check_digits">ISBN-10 Specification</a>
     */
    public static boolean verifyISBN10(@NonNull String ISBN10) {
        if (ISBN10.length() != ISBN_10_LENGTH)
            return false;

        val expectedCheckDigit = calculateISBN10CheckDigit(ISBN10.substring(0, ISBN_10_PREFIX_LENGTH));
        val actualCheckDigit = ISBN10.charAt(ISBN_10_PREFIX_LENGTH);

        return expectedCheckDigit == actualCheckDigit;
    }

    /**
     * @see <a href="https://en.wikipedia.org/wiki/ISBN#ISBN-13_check_digit_calculation">ISBN-13 Specification</a>
     */
    public static boolean verifyISBN13(@NonNull String ISBN13) {
        if (ISBN13.length() != ISBN_13_LENGTH)
            return false;

        val expectedCheckDigit = calculateISBN13CheckDigit(ISBN13.substring(0, ISBN_13_PREFIX_LENGTH));
        val actualCheckDigit = ISBN13.charAt(ISBN_13_PREFIX_LENGTH);

        return expectedCheckDigit == actualCheckDigit;
    }

    /**
     * @see <a href="https://en.wikipedia.org/wiki/ISBN#ISBN-10_check_digit_calculation">ISBN-10 Specification</a>
     */
    public static char calculateISBN10CheckDigit(@NonNull String ISBN10WithoutCheckDigit) {
        if (ISBN10WithoutCheckDigit.length() != ISBN_10_PREFIX_LENGTH)
            throw new IllegalArgumentException(String.format(
                "Invalid length of ISBN-10 (without check digit) '%s': %d",
                ISBN10WithoutCheckDigit, ISBN10WithoutCheckDigit.length()
            ));

        var weightedSum = calculateISBN10WeightedSum(ISBN10WithoutCheckDigit);
        val checkDigit = 11 - (weightedSum % 11);

        return checkDigit == 10 ? 'X' : Character.forDigit(checkDigit, 10);
    }

    /**
     * @see <a href="https://en.wikipedia.org/wiki/ISBN#ISBN-13_check_digit_calculation">ISBN-13 Specification</a>
     */
    public static char calculateISBN13CheckDigit(@NonNull String ISBN13WithoutCheckDigit) {
        if (ISBN13WithoutCheckDigit.length() != ISBN_13_PREFIX_LENGTH) {
            throw new IllegalArgumentException(String.format(
                "Invalid length of ISBN-13 (without check digit) '%s': %d",
                ISBN13WithoutCheckDigit, ISBN13WithoutCheckDigit.length()
            ));
        }

        var weightedSum = calculateISBN13WeightedSum(ISBN13WithoutCheckDigit);
        val checkDigit = 10 - (weightedSum % 10);

        return checkDigit == 10 ? '0' : Character.forDigit(checkDigit, 10);
    }

    private static int calculateISBN10WeightedSum(@NonNull String ISBN10WithoutCheckDigit) {
        var weightedSum = 0;

        for (var index = 0; index < ISBN_10_PREFIX_LENGTH; ++index) {
            val digit = validateAndConvertDigit(ISBN10WithoutCheckDigit.charAt(index), ISBN10WithoutCheckDigit, index);
            val weight = ISBN_10_LENGTH - index;

            weightedSum += digit * weight;
        }

        return weightedSum;
    }

    private static int calculateISBN13WeightedSum(@NonNull String ISBN13WithoutCheckDigit) {
        var weightedSum = 0;

        for (var index = 0; index < ISBN_13_PREFIX_LENGTH; ++index) {
            val digit = validateAndConvertDigit(ISBN13WithoutCheckDigit.charAt(index), ISBN13WithoutCheckDigit, index);
            val weight = (index % 2 == 0) ? 1 : 3;

            weightedSum += digit * weight;
        }

        return weightedSum;
    }

    private static int validateAndConvertDigit(char rawDigit, @NonNull String input, @Unsigned int index) {
        if (!Character.isDigit(rawDigit))
            throw new IllegalArgumentException(String.format(
                "Invalid digit in '%s' at index %d: '%c'", input, index, rawDigit));

        return Character.digit(rawDigit, 10);
    }
}