package org.tomfoolery.infrastructures.utils.contracts;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

public interface ThrowableConsumerController<RequestObject, RequestModel> extends ThrowableConsumer<RequestObject> {
    @NonNull RequestModel getRequestModelFromRequestObject(@NonNull RequestObject requestObject);

    @Override
    void accept(@NonNull RequestObject requestObject) throws Exception;
}
