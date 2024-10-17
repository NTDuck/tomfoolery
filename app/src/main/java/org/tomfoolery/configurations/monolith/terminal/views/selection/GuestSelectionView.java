package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth.CreatePatronAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth.LogUserInActionView;

import java.util.List;

public class GuestSelectionView extends SelectionView {
    private GuestSelectionView() {
        super(List.of(
            Row.of(0, "Exit", null),
            Row.of(1, "Login", LogUserInActionView.class),
            Row.of(2, "Create Patron account", CreatePatronAccountActionView.class)
        ));
    }

    @Override
    protected @NonNull String getPrompt() {
        return "(Guest Selection)";
    }
}
