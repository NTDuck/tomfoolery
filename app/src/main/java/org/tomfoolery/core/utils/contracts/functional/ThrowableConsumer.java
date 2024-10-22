package org.tomfoolery.core.utils.contracts.functional;

import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
public interface ThrowableConsumer<Request> {
    void accept(@NonNull Request request) throws Exception;
}