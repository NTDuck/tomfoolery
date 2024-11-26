package org.tomfoolery.configurations.monolith.console.views.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;

public abstract class BaseView implements Runnable {
    protected final @NonNull IOProvider ioProvider;
    
    protected @Nullable Class<? extends BaseView> nextViewClass;

    protected BaseView(@NonNull IOProvider ioProvider) {
        this.ioProvider = ioProvider;
        this.nextViewClass = null;
    }

    public @Nullable Class<? extends BaseView> getNextViewClass() {
        return this.nextViewClass;
    }
}
