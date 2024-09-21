package org.tomfoolery.core.usecases.utils.function;

@FunctionalInterface
public interface ThrowableSupplier<Response> {
    Response get() throws Exception;
}
