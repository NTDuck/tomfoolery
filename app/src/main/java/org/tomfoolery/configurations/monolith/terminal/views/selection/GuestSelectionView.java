package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.abc.SelectionView;

import java.util.List;

public class GuestSelectionView extends SelectionView {
    public static @NonNull GuestSelectionView of(@NonNull IOHandler ioHandler) {
        return new GuestSelectionView(ioHandler);
    }

    private GuestSelectionView(@NonNull IOHandler ioHandler) {
        super(ioHandler, List.of(
            SelectionItem.of(0, "Exit", null)
        ));
    }

    @Override
    protected @NonNull String getPrompt() {
        return "(Guest Selection)";
    }
}
