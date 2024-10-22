package org.tomfoolery.core.utils.contracts.functional;

import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
public interface ThrowableSupplier<Response> {
    @NonNull Response get() throws Exception;
}