package org.tomfoolery.infrastructures.utils.contracts;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Function;

@FunctionalInterface
public interface Presenter<ResponseModel, ViewModel> extends Function<ResponseModel, ViewModel> {
    @Override
    @NonNull ViewModel apply(@NonNull ResponseModel responseModel);
}
