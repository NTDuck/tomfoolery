package org.tomfoolery.configurations.monolith.console.views.action.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.views.abc.BaseView;

public abstract class BaseActionView extends BaseView {
    protected static @Nullable Class<? extends BaseView> cachedViewClass;   // Only null during the first iteration

    protected BaseActionView(@NonNull IOProvider ioProvider) {
        super(ioProvider);
    }

    @Override
    public final @Nullable Class<? extends BaseView> getNextViewClass() {
        return (cachedViewClass = super.getNextViewClass());
    }

    protected void onException(@NonNull String message, @NonNull String... hints) {
        assert cachedViewClass != null;
        super.onException(message, cachedViewClass, hints);
    }

    protected void onException(@NonNull Exception exception, @NonNull String... hints) {
        assert cachedViewClass != null;
        super.onException(exception, cachedViewClass, hints);
    }
}
