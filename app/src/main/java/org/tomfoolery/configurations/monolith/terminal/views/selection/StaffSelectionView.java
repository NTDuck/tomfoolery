package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.infrastructures.adapters.selection.SelectionAdapter;
import org.tomfoolery.configurations.monolith.terminal.infrastructures.dataproviders.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.AddDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.RemoveDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.UpdateDocumentActionView;

import java.util.List;

public class StaffSelectionView extends UserSelectionView {
    public static @NonNull StaffSelectionView of(@NonNull IOHandler ioHandler) {
        return new StaffSelectionView(ioHandler);
    }

    private StaffSelectionView(@NonNull IOHandler ioHandler) {
        super(ioHandler, List.of(
            SelectionAdapter.Item.of(7, "Add Document", AddDocumentActionView.class),
            SelectionAdapter.Item.of(8, "Update Document", UpdateDocumentActionView.class),
            SelectionAdapter.Item.of(9, "Remove Document", RemoveDocumentActionView.class)
        ));
    }

    @Override
    protected @NonNull String getPrompt() {
        return "Hello staff!";
    }
}
