package org.tomfoolery.core.utils.helpers.adapters.documents;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.utils.helpers.verifiers.documents.ISBNVerifier;

/**
 * @see <a href="https://commons.apache.org/proper/commons-validator/jacoco/org.apache.commons.validator.routines/ISBNValidator.java.html">Jacoco's implementation</a>
 */
@NoArgsConstructor(access = AccessLevel.NONE)
public final class ISBNAdapter {
    /**
     * @see <a href="https://en.wikipedia.org/wiki/ISBN#ISBN-10_check_digit_calculation">ISBN-10 Specification</a>
     */
    public static @NonNull String toISBN10(@NonNull String ISBN13) {
        if (!ISBNVerifier.verifyISBN13(ISBN13))
            throw new IllegalArgumentException(String.format("Invalid ISBN-13: '%s'", ISBN13));

        var ISBN10WithoutCheckDigit = ISBN13.substring(3, 12);
        val checkDigit = ISBNVerifier.calculateISBN10CheckDigit(ISBN10WithoutCheckDigit);

        return ISBN10WithoutCheckDigit + checkDigit;
    }

    /**
     * @see <a href="https://en.wikipedia.org/wiki/ISBN#ISBN-13_check_digit_calculation">ISBN-13 Specification</a>
     */
    public static @NonNull String toISBN13(@NonNull String ISBN10) {
        if (!ISBNVerifier.verifyISBN10(ISBN10))
            throw new IllegalArgumentException(String.format("Invalid ISBN-10: '%s'", ISBN10));

        var ISBN13WithoutCheckDigit = "978" + ISBN10.substring(0, 9);
        val checkDigit = ISBNVerifier.calculateISBN13CheckDigit(ISBN13WithoutCheckDigit);

        return ISBN13WithoutCheckDigit + checkDigit;
    }
}
