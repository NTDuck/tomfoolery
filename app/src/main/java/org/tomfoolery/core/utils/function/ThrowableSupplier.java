package org.tomfoolery.core.utils.function;

@FunctionalInterface
public interface ThrowableSupplier<Response> {
    Response get() throws Exception;
}