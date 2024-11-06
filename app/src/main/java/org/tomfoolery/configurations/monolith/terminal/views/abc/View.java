package org.tomfoolery.configurations.monolith.terminal.views.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface View extends Runnable {
    @Override
    void run();

    @Nullable Class<? extends View> getNextViewClass();

    @NonNull String SUCCESS_MESSAGE_FORMAT = "Success: %s.";
    @NonNull String ERROR_MESSAGE_FORMAT = "Error: %s.";
    @NonNull String PROMPT_MESSAGE_FORMAT = "Enter %s: ";
}
