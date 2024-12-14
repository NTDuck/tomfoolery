package org.tomfoolery.configurations.monolith.console.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.views.action.patron.users.persistence.*;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.borrow.persistence.*;
import org.tomfoolery.configurations.monolith.console.views.action.patron.documents.borrow.retrieval.*;
import org.tomfoolery.configurations.monolith.console.views.action.patron.users.retrieval.GetPatronUsernameAndMetadataActionView;
import org.tomfoolery.configurations.monolith.console.views.action.staff.documents.persistence.*;
import org.tomfoolery.configurations.monolith.console.views.selection.abc.UserSelectionView;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;

import java.util.List;

public final class PatronSelectionView extends UserSelectionView {
    public static @NonNull PatronSelectionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AdministratorRepository administratorRepository, @NonNull PatronRepository patronRepository, @NonNull StaffRepository staffRepository) {
        return new PatronSelectionView(ioProvider, documentRepository, administratorRepository, patronRepository, staffRepository);
    }

    private PatronSelectionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AdministratorRepository administratorRepository, @NonNull PatronRepository patronRepository, @NonNull StaffRepository staffRepository) {
        super(ioProvider, documentRepository, administratorRepository, patronRepository, staffRepository, List.of(
            SelectionItem.of("Borrow a Document", BorrowDocumentActionView.class),
            SelectionItem.of("Return a Document", ReturnDocumentActionView.class),
            SelectionItem.of("Read a Document", ReadBorrowedDocumentActionView.class),
            SelectionItem.of("Show Document borrow status", GetDocumentBorrowStatusActionView.class),
            SelectionItem.of("Show all borrowed Documents", ShowBorrowedDocumentsActionView.class),
            SelectionItem.of("Rate a Document", AddDocumentActionView.class),
            SelectionItem.of("Un-rate a Document", RemoveDocumentActionView.class),
            SelectionItem.of("See account info", GetPatronUsernameAndMetadataActionView.class),
            SelectionItem.of("Change account info", UpdatePatronMetadataActionView.class),
            SelectionItem.of("Change password", UpdatePatronPasswordActionView.class),
            SelectionItem.of("Delete account", DeletePatronAccountActionView.class)
        ));
    }

    @Override
    protected @NonNull String getPrompt() {
        return String.format("%s, %s", super.getPrompt(), "you are a Patron!");
    }
}
