package org.tomfoolery.configurations.monolith.terminal.utils.contract;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface View extends Runnable {
    @Override
    void run();

    @Nullable Class<? extends View> getNextViewClass();
}
