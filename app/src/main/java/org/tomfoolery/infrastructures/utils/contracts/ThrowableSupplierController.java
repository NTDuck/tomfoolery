package org.tomfoolery.infrastructures.utils.contracts;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.utils.contracts.functional.ThrowableSupplier;

public interface ThrowableSupplierController<ResponseModel> extends ThrowableSupplier<ResponseModel> {
    @Override
    @NonNull ResponseModel get() throws Exception;
}
