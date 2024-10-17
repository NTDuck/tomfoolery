package org.tomfoolery.infrastructures.utils.contracts;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface Controller<RequestObject, RequestModel, ResponseModel> {
    @NonNull RequestModel getRequestModelFromRequestObject(@NonNull RequestObject requestObject);
    @NonNull ResponseModel getResponseModelFromRequestObject(@NonNull RequestObject requestObject) throws Exception;
}
