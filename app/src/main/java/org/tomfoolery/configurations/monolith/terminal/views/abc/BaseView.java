package org.tomfoolery.configurations.monolith.terminal.views.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;

public abstract class BaseView implements Runnable {
    protected final @NonNull IOHandler ioHandler;
    protected @Nullable Class<? extends BaseView> nextViewClass;

    protected BaseView(@NonNull IOHandler ioHandler) {
        this.ioHandler = ioHandler;
        this.nextViewClass = null;
    }

    public final @Nullable Class<? extends BaseView> getNextViewClass() {
        return this.nextViewClass;
    }
}
