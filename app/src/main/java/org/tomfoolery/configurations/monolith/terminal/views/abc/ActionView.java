package org.tomfoolery.configurations.monolith.terminal.views.abc;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface ActionView extends View {
    @Override
    @NonNull Class<? extends SelectionView> getNextViewClass();
}
