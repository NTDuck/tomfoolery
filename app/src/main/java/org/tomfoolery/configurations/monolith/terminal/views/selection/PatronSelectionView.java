package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.infrastructures.adapters.selection.SelectionAdapter;
import org.tomfoolery.configurations.monolith.terminal.infrastructures.dataproviders.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.auth.DeletePatronAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.auth.UpdatePatronMetadataActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.auth.UpdatePatronPasswordActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.browse.BorrowDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.browse.GetBorrowedDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.browse.ReturnDocumentActionView;

import java.util.List;

public class PatronSelectionView extends UserSelectionView {
    public static @NonNull PatronSelectionView of(@NonNull IOHandler ioHandler) {
        return new PatronSelectionView(ioHandler);
    }

    private PatronSelectionView(@NonNull IOHandler ioHandler) {
        super(ioHandler, List.of(
            SelectionAdapter.Item.of(7, "Borrow Document", BorrowDocumentActionView.class),
            SelectionAdapter.Item.of(8, "Return Document", ReturnDocumentActionView.class),
            SelectionAdapter.Item.of(9, "Read Document", GetBorrowedDocumentActionView.class),
            SelectionAdapter.Item.of(10, "Update personal info", UpdatePatronMetadataActionView.class),
            SelectionAdapter.Item.of(11, "Change password", UpdatePatronPasswordActionView.class),
            SelectionAdapter.Item.of(12, "Delete account", DeletePatronAccountActionView.class)
        ));
    }

    @Override
    protected @NonNull String getPrompt() {
        return "Hello patron!";
    }
}
