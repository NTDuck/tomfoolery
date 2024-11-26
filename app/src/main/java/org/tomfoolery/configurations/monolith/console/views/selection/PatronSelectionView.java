package org.tomfoolery.configurations.monolith.console.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.views.action.patron.auth.DeletePatronAccountActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.auth.UpdatePatronMetadataActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.auth.UpdatePatronPasswordActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.BorrowDocumentActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.ReadBorrowedDocumentActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.ReturnDocumentActionView;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.ShowBorrowedDocumentsActionView;
import org.tomfoolery.configurations.monolith.console.views.action.staff.documents.AddDocumentActionView;
import org.tomfoolery.configurations.monolith.console.views.action.staff.documents.RemoveDocumentActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.abc.UserSelectionView;

import java.util.List;

public class PatronSelectionView extends UserSelectionView {
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
