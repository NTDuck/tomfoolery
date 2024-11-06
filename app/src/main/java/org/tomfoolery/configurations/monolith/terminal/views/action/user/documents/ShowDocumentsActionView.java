package org.tomfoolery.configurations.monolith.terminal.views.action.user.documents;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.views.action.abc.ActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.abc.BaseSelectionView;

public class ShowDocumentsActionView implements ActionView {
    @Override
    public void run() {

    }

    @Override
    public @NonNull Class<? extends BaseSelectionView> getNextViewClass() {
        return null;
    }
}
