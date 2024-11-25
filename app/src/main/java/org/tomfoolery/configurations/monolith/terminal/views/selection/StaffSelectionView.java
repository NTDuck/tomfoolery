package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.documents.AddDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.documents.RemoveDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.documents.UpdateDocumentContentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.documents.UpdateDocumentMetadataActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.abc.UserSelectionView;

import java.util.List;

public class StaffSelectionView extends UserSelectionView {
    public static @NonNull StaffSelectionView of(@NonNull IOProvider ioProvider) {
        return new StaffSelectionView(ioProvider);
    }

    private StaffSelectionView(@NonNull IOProvider ioProvider) {
        super(ioProvider, List.of(
            SelectionItem.of("Add a Document", AddDocumentActionView.class),
            SelectionItem.of("Change a Document's metadata", UpdateDocumentMetadataActionView.class),
            SelectionItem.of("Change a Document's content", UpdateDocumentContentActionView.class),
            SelectionItem.of("Remove a Document", RemoveDocumentActionView.class)
        ));
    }

    @Override
    protected @NonNull String getPrompt() {
        return String.format("%s, %s", super.getPrompt(), "you are a Staff!");
    }
}
