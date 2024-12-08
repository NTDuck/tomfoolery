package org.tomfoolery.configurations.monolith.console.utils.helpers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class EnumResolver {
    public static @NonNull List<String> getNames(@NonNull Class<? extends Enum<?>> enumClass) {
        return Stream.of(enumClass.getEnumConstants())
            .map(Enum::name)
            .collect(Collectors.toUnmodifiableList());
    }

    /**
     * @param format Should accept exactly 2 {@code args}: {@code enumName} and {@code index}
     */
    public static @NonNull List<String> getEnumeratedNames(@NonNull Class<? extends Enum<?>> enumClass, @NonNull String format, @Unsigned int startIndex) {
        val nonEnumeratedNames = getNames(enumClass);

        return IntStream.range(startIndex, startIndex + nonEnumeratedNames.size())
            .mapToObj(index -> String.format(format, nonEnumeratedNames.get(index - startIndex), index))
            .collect(Collectors.toUnmodifiableList());
    }

    public static @NonNull List<String> getCapitalizedEnumeratedNames(@NonNull Class<? extends Enum<?>> enumClass, @NonNull String format, @Unsigned int startIndex) {
        return getEnumeratedNames(enumClass, format, startIndex).stream()
            .map(EnumResolver::capitalize)
            .collect(Collectors.toUnmodifiableList());
    }

    private static @NonNull String capitalize(@NonNull String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
