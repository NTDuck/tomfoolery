package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.adapters.selection.SelectionAdapter;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.AddDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.RemoveDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.UpdateDocumentActionView;

import java.util.List;

public class StaffSelectionView extends UserSelectionView {
    private StaffSelectionView() {
        super(List.of(
            SelectionAdapter.Item.of(7, "Add Document", AddDocumentActionView.class),
            SelectionAdapter.Item.of(8, "Update Document", UpdateDocumentActionView.class),
            SelectionAdapter.Item.of(9, "Remove Document", RemoveDocumentActionView.class)
        ));
    }

    public static @NonNull StaffSelectionView of() {
        return new StaffSelectionView();
    }

    @Override
    protected @NonNull String getPrompt() {
        return "Hello staff!";
    }
}
