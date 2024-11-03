package org.tomfoolery.configurations.monolith.terminal.utils.containers;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.terminal.views.abc.View;

import java.util.HashMap;
import java.util.Map;

public class Views {
    private final @NonNull Map<Class<? extends View>, View> views = new HashMap<>();

    private Views(@NonNull View... views) {
        for (val view : views)
            this.addView(view);
    }

    public static @NonNull Views of(@NonNull View... views) {
        return new Views(views);
    }

    private void addView(@NonNull View view) {
        this.views.put(view.getClass(), view);
    }

    public @Nullable View getViewByClass(@NonNull Class<? extends View> viewClass) {
        return this.views.get(viewClass);
    }
}
