package org.tomfoolery.configurations.monolith.terminal.utils.dataclasses;

import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.terminal.views.abc.BaseView;

@Value(staticConstructor = "of")
public class SelectionItem {
    @NonNull String label;
    @Nullable Class<? extends BaseView> viewClass;
}
