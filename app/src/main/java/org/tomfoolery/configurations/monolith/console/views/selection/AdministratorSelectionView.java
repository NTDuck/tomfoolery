package org.tomfoolery.configurations.monolith.console.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.views.action.administrator.users.persistence.*;
import org.tomfoolery.configurations.monolith.console.views.action.administrator.users.retrieval.*;
import org.tomfoolery.configurations.monolith.console.views.action.administrator.users.search.*;
import org.tomfoolery.configurations.monolith.console.views.selection.abc.UserSelectionView;

import java.util.List;

public final class AdministratorSelectionView extends UserSelectionView {
    public static @NonNull AdministratorSelectionView of(@NonNull IOProvider ioProvider) {
        return new AdministratorSelectionView(ioProvider);
    }

    private AdministratorSelectionView(@NonNull IOProvider ioProvider) {
        super(ioProvider, List.of(
            SelectionItem.of("Create Staff account", CreateStaffAccountActionView.class),
            SelectionItem.of("Update Staff credentials", UpdateStaffCredentialsActionView.class),
            SelectionItem.of("Delete Staff account", DeleteStaffAccountActionView.class),
            SelectionItem.of("Get Administrator details", GetAdministratorByIdActionView.class),
            SelectionItem.of("Get Staff details", GetStaffByIdActionView.class),
            SelectionItem.of("Get Patron details", GetPatronByIdActionView.class),
            SelectionItem.of("Show all Administrators", ShowAdministratorAccountsActionView.class),
            SelectionItem.of("Show all Staff", ShowStaffAccountsActionView.class),
            SelectionItem.of("Show all Patrons", ShowPatronAccountsActionView.class),
            SelectionItem.of("Search for Administrators", SearchAdministratorsByUsernameActionView.class),
            SelectionItem.of("Search for Staff", SearchStaffByUsernameActionView.class),
            SelectionItem.of("Search for Patrons", SearchPatronsByUsernameActionView.class)
        ));
    }

    @Override
    protected @NonNull String getPrompt() {
        return String.format("%s, %s", super.getPrompt(), "you are an Administrator!");
    }
}
