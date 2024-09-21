package org.tomfoolery.core.utils.function;

@FunctionalInterface
public interface ThrowableConsumer<Request> {
    void accept(Request request) throws Exception;
}
