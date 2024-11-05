package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.auth.DeletePatronAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.auth.UpdatePatronMetadataActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.auth.UpdatePatronPasswordActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.browse.BorrowDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.browse.GetBorrowedDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.browse.ReturnDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.abc.UserSelectionView;

import java.util.List;

public class PatronSelectionView extends UserSelectionView {
    public static @NonNull PatronSelectionView of(@NonNull IOHandler ioHandler) {
        return new PatronSelectionView(ioHandler);
    }

    private PatronSelectionView(@NonNull IOHandler ioHandler) {
        super(ioHandler, List.of(
            SelectionItem.of(7, "Borrow Document", BorrowDocumentActionView.class),
            SelectionItem.of(8, "Return Document", ReturnDocumentActionView.class),
            SelectionItem.of(9, "Read Document", GetBorrowedDocumentActionView.class),
            SelectionItem.of(10, "Update personal info", UpdatePatronMetadataActionView.class),
            SelectionItem.of(11, "Change password", UpdatePatronPasswordActionView.class),
            SelectionItem.of(12, "Delete account", DeletePatronAccountActionView.class)
        ));
    }

    @Override
    protected @NonNull String getPrompt() {
        return "Hello patron!";
    }
}
