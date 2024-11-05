package org.tomfoolery.configurations.monolith.terminal.views.selection.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.abc.SelectionView;

import java.util.List;
import java.util.stream.Stream;

public abstract class UserSelectionView extends SelectionView {
    private static final List<SelectionItem> DEFAULT_SELECTION_ITEMS = List.of(
        SelectionItem.of(0, "Exit", null)
    );

    private static @NonNull List<SelectionItem> getCombinedItems(@NonNull List<SelectionItem> additionalSelectionItems) {
        return Stream.concat(DEFAULT_SELECTION_ITEMS.stream(), additionalSelectionItems.stream()).toList();
    }

    protected UserSelectionView(@NonNull IOHandler ioHandler, @NonNull List<SelectionItem> additionalSelectionItems) {
        super(ioHandler, getCombinedItems(additionalSelectionItems));
    }
}
