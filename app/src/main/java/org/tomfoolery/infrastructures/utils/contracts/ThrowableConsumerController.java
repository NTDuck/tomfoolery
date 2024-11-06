package org.tomfoolery.infrastructures.utils.contracts;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

public interface ThrowableConsumerController<RequestObject> extends ThrowableConsumer<RequestObject> {
    @Override
    void accept(@NonNull RequestObject requestObject) throws Exception;
}
