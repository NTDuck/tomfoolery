package org.tomfoolery.configurations.monolith.terminal.views.action.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.terminal.views.abc.BaseView;

public abstract class BaseActionView extends BaseView {
    protected static @Nullable Class<? extends BaseView> cachedViewClass;

    protected BaseActionView(@NonNull IOProvider ioProvider) {
        super(ioProvider);
    }

    @Override
    public final @Nullable Class<? extends BaseView> getNextViewClass() {
        return (cachedViewClass = super.getNextViewClass());
    }
}
