package org.tomfoolery.core.utils.function;

@FunctionalInterface
public interface ThrowableFunction<Request, Response> {
    Response apply(Request request) throws Exception;
}
