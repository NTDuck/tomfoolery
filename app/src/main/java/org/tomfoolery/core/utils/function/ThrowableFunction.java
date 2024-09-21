package org.tomfoolery.core.usecases.utils.function;

@FunctionalInterface
public interface ThrowableFunction<Request, Response> {
    Response apply(Request request) throws Exception;
}
