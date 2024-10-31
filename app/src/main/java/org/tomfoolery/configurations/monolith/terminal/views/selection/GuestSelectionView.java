package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.adapters.selection.SelectionAdapter;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth.CreatePatronAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth.LogUserInActionView;

import java.util.List;

public class GuestSelectionView extends SelectionView {
    private GuestSelectionView() {
        super(List.of(
            SelectionAdapter.Item.of(0, "Exit", null),
            SelectionAdapter.Item.of(1, "Login", LogUserInActionView.class),
            SelectionAdapter.Item.of(2, "Create Patron account", CreatePatronAccountActionView.class)
        ));
    }

    public static @NonNull GuestSelectionView of() {
        return new GuestSelectionView();
    }

    @Override
    protected @NonNull String getPrompt() {
        return "(Guest Selection)";
    }
}
