package org.tomfoolery.core.utils.contracts.functional;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Function;

public interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);

    default <S> TriFunction<T, U, V, S> andThen(@NonNull Function<? super R, ? extends S> after) {
        return (T t, U u, V v) -> after.apply(apply(t, u, v));
    }
}
