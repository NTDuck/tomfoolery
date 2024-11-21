package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.selection.abc.BaseSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth.CreatePatronAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth.LogUserInActionView;

import java.util.List;

public class GuestSelectionView extends BaseSelectionView {
    public static @NonNull GuestSelectionView of(@NonNull IOHandler ioHandler) {
        return new GuestSelectionView(ioHandler);
    }

    private GuestSelectionView(@NonNull IOHandler ioHandler) {
        super(ioHandler, List.of(
            SelectionItem.of("Exit", null),
            SelectionItem.of("Create Patron account", CreatePatronAccountActionView.class),
            SelectionItem.of("Login", LogUserInActionView.class)
        ));
    }

    @Override
    protected @NonNull String getPrompt() {
        return String.format("%s, %s", super.getPrompt(), "you are a Guest!");
    }
}
