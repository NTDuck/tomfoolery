package org.tomfoolery.infrastructures.utils.contracts;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Function;

public interface Controller<RequestObject, RequestModel> extends Function<RequestObject, RequestModel> {
    @Override
    @NonNull RequestModel apply(@NonNull RequestObject requestObject);
}
