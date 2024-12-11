package org.tomfoolery.configurations.monolith.console.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.views.selection.abc.BaseSelectionView;
import org.tomfoolery.configurations.monolith.console.views.action.guest.users.persistence.CreatePatronAccountActionView;
import org.tomfoolery.configurations.monolith.console.views.action.guest.users.authentication.LogUserInByCredentialsActionView;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;

import java.util.List;

public final class GuestSelectionView extends BaseSelectionView {
    public static @NonNull GuestSelectionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AdministratorRepository administratorRepository, @NonNull PatronRepository patronRepository, @NonNull StaffRepository staffRepository) {
        return new GuestSelectionView(ioProvider, documentRepository, administratorRepository, patronRepository, staffRepository);
    }

    private GuestSelectionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AdministratorRepository administratorRepository, @NonNull PatronRepository patronRepository, @NonNull StaffRepository staffRepository) {
        super(ioProvider, documentRepository, administratorRepository, patronRepository, staffRepository, List.of(
            SelectionItem.of("Exit", null),
            SelectionItem.of("Create Patron account", CreatePatronAccountActionView.class),
            SelectionItem.of("Login", LogUserInByCredentialsActionView.class)
        ));
    }

    @Override
    protected @NonNull String getPrompt() {
        return String.format("%s, %s", super.getPrompt(), "you are a Guest!");
    }
}
