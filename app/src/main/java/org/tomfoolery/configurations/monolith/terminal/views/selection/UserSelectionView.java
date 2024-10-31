package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.adapters.selection.SelectionAdapter;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.auth.LogUserOutActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.browse.*;

import java.util.List;
import java.util.stream.Stream;

public abstract class UserSelectionView extends SelectionView {
    private static final List<SelectionAdapter.Item> ITEMS = List.of(
        SelectionAdapter.Item.of(0, "Exit", null),
        SelectionAdapter.Item.of(1, "Log out", LogUserOutActionView.class),
        SelectionAdapter.Item.of(2, "Get Document info", GetDocumentByIdActionView.class),
        SelectionAdapter.Item.of(3, "Search Documents by Criterion", SearchDocumentsByCriterionActionView.class),
        SelectionAdapter.Item.of(6, "Show all Documents", ShowDocumentsActionView.class)
    );

    private static <T> @NonNull List<T> join(List<T> firstList, List<T> secondList) {
        return Stream.concat(firstList.stream(), secondList.stream()).toList();
    }

    public UserSelectionView(@NonNull List<SelectionAdapter.Item> items) {
        super(join(UserSelectionView.ITEMS, items));
    }
}
