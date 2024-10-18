package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.auth.LogUserOutActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.browse.*;

import java.util.List;
import java.util.stream.Stream;

public abstract class UserSelectionView extends SelectionView {
    private static final List<Row> rows = List.of(
        Row.of(0, "Exit", null),
        Row.of(1, "Log out", LogUserOutActionView.class),
        Row.of(2, "Get Document info", GetDocumentByIdActionView.class),
        Row.of(3, "Search Documents by Title", SearchDocumentsByTitleActionView.class),
        Row.of(4, "Search Documents by Author", SearchDocumentsByAuthorActionView.class),
        Row.of(5, "Search Documents by Genre", SearchDocumentsByGenreActionView.class),
        Row.of(6, "Show all Documents", ShowDocumentsActionView.class)
    );

    private static <T> @NonNull List<T> join(List<T> firstList, List<T> secondList) {
        return Stream.concat(firstList.stream(), secondList.stream()).toList();
    }

    public UserSelectionView(@NonNull List<Row> rows) {
        super(join(UserSelectionView.rows, rows));
    }
}
