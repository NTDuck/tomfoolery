package org.tomfoolery.configurations.monolith.terminal.views.action.user.browse;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.contracts.ActionView;
import org.tomfoolery.configurations.monolith.terminal.utils.contracts.SelectionView;

public class ShowDocumentsActionView implements ActionView {
    @Override
    public void run() {
        // ...
    }

    @Override
    public @NonNull Class<? extends SelectionView> getNextViewClass() {
        return null;
    }
}
