package org.tomfoolery.core.usecases.utils;

@FunctionalInterface
public interface ThrowableFunction<Request, Response> {
    Response apply(Request request) throws Exception;
}
