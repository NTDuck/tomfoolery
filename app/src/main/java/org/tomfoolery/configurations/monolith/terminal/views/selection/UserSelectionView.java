package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.infrastructures.adapters.selection.SelectionAdapter;
import org.tomfoolery.configurations.monolith.terminal.infrastructures.dataproviders.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.abc.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.auth.LogUserOutActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.browse.*;

import java.util.List;
import java.util.stream.Stream;

public abstract class UserSelectionView extends SelectionView {
    private static final List<SelectionAdapter.Item> DEFAULT_ITEMS = List.of(
        SelectionAdapter.Item.of(0, "Exit", null),
        SelectionAdapter.Item.of(1, "Log out", LogUserOutActionView.class),
        SelectionAdapter.Item.of(2, "Get Document info", GetDocumentByIdActionView.class),
        SelectionAdapter.Item.of(3, "Search Documents by Criterion", SearchDocumentsByCriterionActionView.class),
        SelectionAdapter.Item.of(6, "Show all Documents", ShowDocumentsActionView.class)
    );

    private static @NonNull List<SelectionAdapter.Item> getCombinedItems(@NonNull List<SelectionAdapter.Item> additionalItems) {
        return Stream.concat(DEFAULT_ITEMS.stream(), additionalItems.stream()).toList();
    }

    protected UserSelectionView(@NonNull IOHandler ioHandler, @NonNull List<SelectionAdapter.Item> additionalItems) {
        super(ioHandler, getCombinedItems(additionalItems));
    }
}
