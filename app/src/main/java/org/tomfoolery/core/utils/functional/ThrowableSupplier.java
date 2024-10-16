package org.tomfoolery.core.utils.functional;

import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
public interface ThrowableSupplier<Response> {
    @NonNull Response get() throws Exception;
}