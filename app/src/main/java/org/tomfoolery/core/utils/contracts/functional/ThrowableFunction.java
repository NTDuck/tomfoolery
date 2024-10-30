package org.tomfoolery.core.utils.contracts.functional;

import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
public interface ThrowableFunction<Request, Response> {
    @NonNull Response apply(@NonNull Request request) throws Exception;
}