package org.tomfoolery.core.usecases.utils;

@FunctionalInterface
public interface ThrowableSupplier<Response> {
    Response get() throws Exception;
}
