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

    protected static final @NonNull String SUCCESS_MESSAGE_FORMAT = "Success: %s.";
    protected static final @NonNull String ERROR_MESSAGE_FORMAT = "Error: %s.";
    protected static final @NonNull String PROMPT_MESSAGE_FORMAT = "Enter %s: ";

    protected static final @NonNull String USERNAME_CONSTRAINT_MESSAGE = "Username must be 8-16 characters long; contains only lowercase letters, digits, and underscores; must not start with a digit or end with an underscore.";
    protected static final @NonNull String PASSWORD_CONSTRAINT_MESSAGE = "Password must be 8-32 characters long; contains only letters, digits, and underscores.";

    protected static final int MAX_PAGE_SIZE = 5;
}
