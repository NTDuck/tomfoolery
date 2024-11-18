package org.tomfoolery.configurations.monolith.terminal.utils.helpers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class EnumResolver {
    public static <T extends Enum<T>> @NonNull List<String> getNames(Class<T> enumClass) {
        return Stream.of(enumClass.getEnumConstants())
            .map(Enum::name)
            .collect(Collectors.toUnmodifiableList());
    }
}
