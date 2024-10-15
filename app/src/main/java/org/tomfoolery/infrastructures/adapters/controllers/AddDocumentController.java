package org.tomfoolery.infrastructures.adapters.controllers;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.usecases.external.staff.AddDocumentUseCase;
import org.tomfoolery.infrastructures.adapters.contracts.FunctionContract;

@RequiredArgsConstructor(staticName = "of")
public class AddDocumentController implements FunctionContract.Controller<AddDocumentController.RequestObject, AddDocumentUseCase.Response> {
    private final @NonNull AddDocumentUseCase addDocumentUseCase;

    @Override
    public AddDocumentUseCase.@NonNull Response getResponseModel(@NonNull RequestObject requestObject) {
        return null;
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {

    }
}
