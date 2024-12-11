package org.tomfoolery.configurations.monolith.console.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.views.action.staff.documents.persistence.*;
import org.tomfoolery.configurations.monolith.console.views.action.staff.documents.retrieval.*;
import org.tomfoolery.configurations.monolith.console.views.selection.abc.UserSelectionView;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;

import java.util.List;

public final class StaffSelectionView extends UserSelectionView {
    public static @NonNull StaffSelectionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AdministratorRepository administratorRepository, @NonNull PatronRepository patronRepository, @NonNull StaffRepository staffRepository) {
        return new StaffSelectionView(ioProvider, documentRepository, administratorRepository, patronRepository, staffRepository);
    }

    private StaffSelectionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AdministratorRepository administratorRepository, @NonNull PatronRepository patronRepository, @NonNull StaffRepository staffRepository) {
        super(ioProvider, documentRepository, administratorRepository, patronRepository, staffRepository, List.of(
            SelectionItem.of("Add a Document", AddDocumentActionView.class),
            SelectionItem.of("Remove a Document", RemoveDocumentActionView.class),
            SelectionItem.of("Change a Document's metadata", UpdateDocumentMetadataActionView.class),
            SelectionItem.of("Change a Document's content", UpdateDocumentContentActionView.class),
            SelectionItem.of("Change a Document's cover image", UpdateDocumentCoverImageActionView.class),
            SelectionItem.of("Show all Documents with missing content", ShowDocumentsWithoutContentActionView.class)
        ));
    }

    @Override
    protected @NonNull String getPrompt() {
        return String.format("%s, %s", super.getPrompt(), "you are a Staff!");
    }
}
