package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.documents.AddDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.documents.RemoveDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.documents.UpdateDocumentContentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.documents.UpdateDocumentMetadataActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.abc.UserSelectionView;

import java.util.List;

public class StaffSelectionView extends UserSelectionView {
    public static @NonNull StaffSelectionView of(@NonNull IOHandler ioHandler) {
        return new StaffSelectionView(ioHandler);
    }

    private StaffSelectionView(@NonNull IOHandler ioHandler) {
        super(ioHandler, List.of(
            SelectionItem.of(7, "Add a Document", AddDocumentActionView.class),
            SelectionItem.of(8, "Change a Document's content", UpdateDocumentContentActionView.class),
            SelectionItem.of(9, "Change a Document's metadata", UpdateDocumentMetadataActionView.class),
            SelectionItem.of(10, "Remove a Document", RemoveDocumentActionView.class)
        ));
    }

    @Override
    protected @NonNull String getPrompt() {
        return String.format("%s, %s", super.getPrompt(), "you are a Staff!");
    }
}
