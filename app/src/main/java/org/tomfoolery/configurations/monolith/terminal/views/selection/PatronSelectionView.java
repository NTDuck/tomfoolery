package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.auth.DeletePatronAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.auth.UpdatePatronMetadataActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.auth.UpdatePatronPasswordActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.documents.BorrowDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.documents.ReadBorrowedDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.documents.ReturnDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.patron.documents.ShowBorrowedDocumentsActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.documents.AddDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.staff.documents.RemoveDocumentActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.abc.UserSelectionView;

import java.util.List;

public class PatronSelectionView extends UserSelectionView {
    public static @NonNull PatronSelectionView of(@NonNull IOHandler ioHandler) {
        return new PatronSelectionView(ioHandler);
    }

    private PatronSelectionView(@NonNull IOHandler ioHandler) {
        super(ioHandler, List.of(
            SelectionItem.of(7, "Borrow a Document", BorrowDocumentActionView.class),
            SelectionItem.of(8, "Return a Document", ReturnDocumentActionView.class),
            SelectionItem.of(9, "Show all borrowed Documents", ShowBorrowedDocumentsActionView.class),
            SelectionItem.of(10, "Read a Document", ReadBorrowedDocumentActionView.class),
            SelectionItem.of(11, "Rate a Document", AddDocumentActionView.class),
            SelectionItem.of(12, "Remove rating from a Document", RemoveDocumentActionView.class),
            SelectionItem.of(13, "Change personal info", UpdatePatronMetadataActionView.class),
            SelectionItem.of(14, "Change password", UpdatePatronPasswordActionView.class),
            SelectionItem.of(15, "Delete account", DeletePatronAccountActionView.class)
        ));
    }

    @Override
    protected @NonNull String getPrompt() {
        return String.format("%s, %s", super.getPrompt(), "you are a Patron!");
    }
}
