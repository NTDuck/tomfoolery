package org.tomfoolery.infrastructures.adapters.contracts;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface SupplierContract {
    interface View<ViewModel> {
        void getRequestObject();
        void displayViewModel(@NonNull ViewModel viewModel);
    }

    @FunctionalInterface
    interface Presenter<ViewModel> {
        @NonNull ViewModel getViewModel();
    }
}
