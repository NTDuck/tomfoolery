package org.tomfoolery.infrastructures.adapters.contracts;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface ConsumerContract {
    interface View<RequestObject> {
        @NonNull RequestObject getRequestObject();
        void displayViewModel();
    }

    @FunctionalInterface
    interface Controller<RequestObject> {
        void consumeRequestObject(@NonNull RequestObject requestObject);
    }
}
