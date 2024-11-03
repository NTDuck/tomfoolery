package org.tomfoolery.configurations.monolith.terminal.views.action.patron.auth;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.views.abc.ActionView;
import org.tomfoolery.configurations.monolith.terminal.views.abc.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.AdministratorSelectionView;

public class UpdatePatronMetadataActionView implements ActionView {
    @Override
    public void run() {
        System.out.println("Available in future releases :3");
    }

    @Override
    public @NonNull Class<? extends SelectionView> getNextViewClass() {
        return AdministratorSelectionView.class;
    }
}
