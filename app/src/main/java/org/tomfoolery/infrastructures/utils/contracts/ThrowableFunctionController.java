package org.tomfoolery.infrastructures.utils.contracts;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

public interface ThrowableFunctionController<RequestObject, RequestModel, ResponseModel> extends ThrowableFunction<RequestObject, ResponseModel> {
    @NonNull RequestModel getRequestModelFromRequestObject(@NonNull RequestObject requestObject);

    @Override
    @NonNull ResponseModel apply(@NonNull RequestObject requestObject) throws Exception;
}
