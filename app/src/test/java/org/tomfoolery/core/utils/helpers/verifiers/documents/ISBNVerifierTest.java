package org.tomfoolery.core.utils.helpers.verifiers.documents;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

@Test(groups = { "unit", "verifiers", "isbn" })
public class ISBNVerifierTest {
    void GivenValidISBN10_WhenVerifying_ExpectTrue(@NonNull String validISBN10) {
        //
    }

    void GivenInvalidISBN13_WhenVerifying_ExpectFalse(@NonNull String invalidISBN10) {
        //
    }

    void GivenValidISBN13_WhenVerifying_ExpectTrue(@NonNull String validISBN13) {
        //
    }

    void GivenInvalidISBN10_WhenVerifying_ExpectFalse(@NonNull String invalidISBN13) {
        //
    }

    public static class Params {
        @DataProvider
        public static Iterator<Object[]> provide() {
            return List.of(
                new Object[] { "978-3-16-148410-0" },
                new Object[] { "978-3-16-148410-1" },
                new Object[] { "978-3-16-148410-2" },
                new Object[] { "978-3-16-148410-3" },
                new Object[] { "978-3-16-148410-4" },
                new Object[] { "978-3-16-148410-5" },
                new Object[] { "978-3-16-148410-6" },
                new Object[] { "978-3-16-148410-7" },
                new Object[] { "978-3-16-148410-8" },
                new Object[] { "978-3-16-148410-9" }
            ).iterator();
        }
    }
}
