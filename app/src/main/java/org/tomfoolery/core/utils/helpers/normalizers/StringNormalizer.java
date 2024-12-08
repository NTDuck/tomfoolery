package org.tomfoolery.core.utils.helpers.normalizers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.Normalizer;
import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class StringNormalizer {
    public static @NonNull String normalize(@NonNull String s) {
        s = decomposeDiacritics(s);
        s = removeDiacriticalMarks(s);
        s = removeWhitespace(s);
        s = lowercase(s);

        return s;
    }

    private static @NonNull String decomposeDiacritics(@NonNull String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD);
    }

    private static @NonNull String removeDiacriticalMarks(@NonNull String s) {
        return s.replaceAll("\\p{M}", "");
    }

    private static @NonNull String removeWhitespace(@NonNull String s) {
        return s.replaceAll("\\s+", "");
    }

    private static @NonNull String lowercase(@NonNull String s) {
        return s.toLowerCase(Locale.ROOT);
    }
}
