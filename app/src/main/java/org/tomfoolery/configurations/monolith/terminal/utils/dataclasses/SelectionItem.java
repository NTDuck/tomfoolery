package org.tomfoolery.configurations.monolith.terminal.utils.dataclasses;

import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.terminal.views.abc.View;

@Value(staticConstructor = "of")
public class SelectionItem {
    int index;
    @NonNull String label;
    @Nullable Class<? extends View> viewClass;
}