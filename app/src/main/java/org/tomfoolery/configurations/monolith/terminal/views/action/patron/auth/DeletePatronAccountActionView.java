package org.tomfoolery.configurations.monolith.terminal.views.action.patron.auth;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.contracts.ActionView;
import org.tomfoolery.configurations.monolith.terminal.utils.contracts.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.AdministratorSelectionView;

public class DeletePatronAccountActionView implements ActionView {
    @Override
    public void run() {
        System.out.println("Available in future releases :3");
    }

    @Override
    public @NonNull Class<? extends SelectionView> getNextViewClass() {
        return AdministratorSelectionView.class;
    }
}
