package org.tomfoolery.configurations.monolith.console.views.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;

public abstract class BaseView implements Runnable {
    protected final @NonNull IOProvider ioProvider;
    
    protected @Nullable Class<? extends BaseView> nextViewClass;

    protected BaseView(@NonNull IOProvider ioProvider) {
        this.ioProvider = ioProvider;
    }

    public @Nullable Class<? extends BaseView> getNextViewClass() {
        return this.nextViewClass;
    }

    protected void onException(@NonNull String message, @NonNull Class<? extends BaseView> nextViewClass, @NonNull String... hints) {
        this.nextViewClass = nextViewClass;

        this.ioProvider.writeLine(Message.Format.ERROR, message);

        if (hints.length == 0)
            return;

        this.ioProvider.writeLine("Hints:");
        this.ioProvider.writeLine("- " + String.join("\n- ", hints));
    }

    protected void onException(@NonNull Exception exception, @NonNull Class<? extends BaseView> nextViewClass, @NonNull String... hints) {
        this.onException(getMessageFromException(exception), nextViewClass, hints);
    }

    private static @NonNull String getMessageFromException(@NonNull Exception exception) {
        return String.format("%s%s", exception.getClass().getSimpleName(),
            exception.getLocalizedMessage() != null ? String.format(" (%s)", exception.getLocalizedMessage()) : "");
    }
}
