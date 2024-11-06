package org.tomfoolery.configurations.monolith.terminal.utils.containers;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.terminal.views.abc.BaseView;

import java.util.HashMap;
import java.util.Map;

public class Views {
    private final @NonNull Map<Class<? extends BaseView>, BaseView> views = new HashMap<>();

    private Views(@NonNull BaseView... baseViews) {
        for (val view : baseViews)
            this.addView(view);
    }

    public static @NonNull Views of(@NonNull BaseView... baseViews) {
        return new Views(baseViews);
    }

    private void addView(@NonNull BaseView baseView) {
        this.views.put(baseView.getClass(), baseView);
    }

    public @Nullable BaseView getViewByClass(@NonNull Class<? extends BaseView> viewClass) {
        return this.views.get(viewClass);
    }
}
