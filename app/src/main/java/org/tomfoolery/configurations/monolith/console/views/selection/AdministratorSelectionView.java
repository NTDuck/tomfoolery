package org.tomfoolery.configurations.monolith.console.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.views.action.admin.auth.CreateStaffAccountActionView;
import org.tomfoolery.configurations.monolith.console.views.action.admin.auth.DeleteStaffAccountActionView;
import org.tomfoolery.configurations.monolith.console.views.action.admin.auth.UpdateStaffCredentialsActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.abc.UserSelectionView;

import java.util.List;

public class AdministratorSelectionView extends UserSelectionView {
    public static @NonNull AdministratorSelectionView of(@NonNull IOProvider ioProvider) {
        return new AdministratorSelectionView(ioProvider);
    }

    private AdministratorSelectionView(@NonNull IOProvider ioProvider) {
        super(ioProvider, List.of(
            SelectionItem.of("Create Staff account", CreateStaffAccountActionView.class),
            SelectionItem.of("Update Staff credentials", UpdateStaffCredentialsActionView.class),
            SelectionItem.of("Delete Staff account", DeleteStaffAccountActionView.class)
        ));
    }

    @Override
    protected @NonNull String getPrompt() {
        return String.format("%s, %s", super.getPrompt(), "you are an Administrator!");
    }
}
