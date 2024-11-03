package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.adapters.SelectionAdapter;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.abc.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth.CreatePatronAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth.LogUserInActionView;

import java.util.List;

public class GuestSelectionView extends SelectionView {
    public static @NonNull GuestSelectionView of(@NonNull IOHandler ioHandler) {
        return new GuestSelectionView(ioHandler);
    }

    private GuestSelectionView(@NonNull IOHandler ioHandler) {
        super(ioHandler, List.of(
            SelectionAdapter.Item.of(0, "Exit", null),
            SelectionAdapter.Item.of(1, "Login", LogUserInActionView.class),
            SelectionAdapter.Item.of(2, "Create Patron account", CreatePatronAccountActionView.class)
        ));
    }

    @Override
    protected @NonNull String getPrompt() {
        return "(Guest Selection)";
    }
}
