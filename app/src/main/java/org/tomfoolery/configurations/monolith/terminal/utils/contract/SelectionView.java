package org.tomfoolery.configurations.monolith.terminal.utils.contract;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface SelectionView extends View {
    @Override
    @Nullable Class<? extends ActionView> getNextViewClass();
}
