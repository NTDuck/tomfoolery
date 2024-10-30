package org.tomfoolery.infrastructures.utils.contracts;

import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
public interface Presenter<ResponseModel, ViewModel> {
    @NonNull ViewModel getViewModelFromResponseModel(@NonNull ResponseModel responseModel);
}
