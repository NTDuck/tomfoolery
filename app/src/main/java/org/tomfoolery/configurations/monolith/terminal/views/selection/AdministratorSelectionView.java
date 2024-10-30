package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.adapters.selection.SelectionAdapter;
import org.tomfoolery.configurations.monolith.terminal.views.action.admin.auth.CreateStaffAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.admin.auth.DeleteStaffAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.admin.auth.UpdateStaffCredentialsActionView;

import java.util.List;

public class AdministratorSelectionView extends UserSelectionView {
    private AdministratorSelectionView() {
        super(List.of(
            SelectionAdapter.Item.of(7, "Create Staff account", CreateStaffAccountActionView.class),
            SelectionAdapter.Item.of(8, "Update Staff credentials", UpdateStaffCredentialsActionView.class),
            SelectionAdapter.Item.of(9, "Delete Staff account", DeleteStaffAccountActionView.class)
        ));
    }

    public static @NonNull AdministratorSelectionView of() {
        return new AdministratorSelectionView();
    }

    @Override
    protected @NonNull String getPrompt() {
        return "Hello admin!";
    }
}
