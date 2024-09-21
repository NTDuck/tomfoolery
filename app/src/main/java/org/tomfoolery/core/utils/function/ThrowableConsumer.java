package org.tomfoolery.core.usecases.utils.function;

import java.util.Objects;

@FunctionalInterface
public interface ThrowableConsumer<Request> {
    void accept(Request request) throws Exception;

    default ThrowableConsumer<Request> andThen(ThrowableConsumer<? super Request> after) {
        Objects.requireNonNull(after);
        return (Request request) -> { accept(request); after.accept(request); };
    }
}
