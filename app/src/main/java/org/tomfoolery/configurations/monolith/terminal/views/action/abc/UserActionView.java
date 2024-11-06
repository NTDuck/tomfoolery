package org.tomfoolery.configurations.monolith.terminal.views.action.abc;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.terminal.views.abc.BaseView;

public class UserActionView extends BaseView {
    @Override
    public @Nullable Class<? extends BaseView> getNextViewClass() {
        return null;
    }

    @Override
    public void run() {

    }
}
