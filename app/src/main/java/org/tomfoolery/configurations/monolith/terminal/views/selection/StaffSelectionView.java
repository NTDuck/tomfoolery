package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.AddDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.RemoveDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.UpdateDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.abc.UserSelectionView;

import java.util.List;

public class StaffSelectionView extends UserSelectionView {
    public static @NonNull StaffSelectionView of(@NonNull IOHandler ioHandler) {
        return new StaffSelectionView(ioHandler);
    }

    private StaffSelectionView(@NonNull IOHandler ioHandler) {
        super(ioHandler, List.of(
            SelectionItem.of(7, "Add Document", AddDocumentActionView.class),
            SelectionItem.of(8, "Update Document", UpdateDocumentActionView.class),
            SelectionItem.of(9, "Remove Document", RemoveDocumentActionView.class)
        ));
    }

    @Override
    protected @NonNull String getPrompt() {
        return "Hello staff!";
    }
}
