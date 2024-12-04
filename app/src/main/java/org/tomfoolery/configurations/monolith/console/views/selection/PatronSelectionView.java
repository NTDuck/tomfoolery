package org.tomfoolery.configurations.monolith.console.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.views.action.patron.users.persistence.DeletePatronAccountActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.users.persistence.UpdatePatronMetadataActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.users.persistence.UpdatePatronPasswordActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.borrow.persistence.BorrowDocumentActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.borrow.retrieval.ReadBorrowedDocumentActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.borrow.persistence.ReturnDocumentActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.borrow.retrieval.ShowBorrowedDocumentsActionView;
import org.tomfoolery.configurations.monolith.console.views.action.staff.documents.persistence.AddDocumentActionView;
import org.tomfoolery.configurations.monolith.console.views.action.staff.documents.persistence.RemoveDocumentActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.abc.UserSelectionView;

import java.util.List;

public final class PatronSelectionView extends UserSelectionView {
    public static @NonNull PatronSelectionView of(@NonNull IOProvider ioProvider) {
        return new PatronSelectionView(ioProvider);
    }

    private PatronSelectionView(@NonNull IOProvider ioProvider) {
        super(ioProvider, List.of(
            SelectionItem.of("Borrow a Document", BorrowDocumentActionView.class),
            SelectionItem.of("Return a Document", ReturnDocumentActionView.class),
            SelectionItem.of("Show all borrowed Documents", ShowBorrowedDocumentsActionView.class),
            SelectionItem.of("Read a Document", ReadBorrowedDocumentActionView.class),
            SelectionItem.of("Rate a Document", AddDocumentActionView.class),
            SelectionItem.of("Remove rating from a Document", RemoveDocumentActionView.class),
            SelectionItem.of("Change personal info", UpdatePatronMetadataActionView.class),
            SelectionItem.of("Change password", UpdatePatronPasswordActionView.class),
            SelectionItem.of("Delete account", DeletePatronAccountActionView.class)
        ));
    }

    @Override
    protected @NonNull String getPrompt() {
        return String.format("%s, %s", super.getPrompt(), "you are a Patron!");
    }
}
