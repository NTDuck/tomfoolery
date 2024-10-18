package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.auth.DeletePatronAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.auth.UpdatePatronMetadataActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.auth.UpdatePatronPasswordActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.browse.BorrowDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.browse.GetBorrowedDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.browse.ReturnDocumentActionView;

import java.util.List;

public class PatronSelectionView extends UserSelectionView {
    private PatronSelectionView() {
        super(List.of(
            Row.of(7, "Borrow Document", BorrowDocumentActionView.class),
            Row.of(8, "Return Document", ReturnDocumentActionView.class),
            Row.of(9, "Read Document", GetBorrowedDocumentActionView.class),
            Row.of(10, "Update personal info", UpdatePatronMetadataActionView.class),
            Row.of(11, "Change password", UpdatePatronPasswordActionView.class),
            Row.of(12, "Delete account", DeletePatronAccountActionView.class)
        ));
    }

    public static @NonNull PatronSelectionView of() {
        return new PatronSelectionView();
    }

    @Override
    protected @NonNull String getPrompt() {
        return "Hello patron!";
    }
}
