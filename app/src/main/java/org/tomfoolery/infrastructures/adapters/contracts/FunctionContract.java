package org.tomfoolery.infrastructures.adapters.contracts;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface FunctionContract {
    interface View<RequestObject, ViewModel> {
        @NonNull RequestObject getRequestObject();
        void displayViewModel(@NonNull ViewModel viewModel);
    }

    @FunctionalInterface
    interface Controller<RequestObject, ResponseModel> {
        @NonNull ResponseModel getResponseModel(@NonNull RequestObject requestObject);
    }

    @FunctionalInterface
    interface Presenter<ResponseModel, ViewModel> {
        @NonNull ViewModel getViewModel(@NonNull ResponseModel responseModel);
    }
}
