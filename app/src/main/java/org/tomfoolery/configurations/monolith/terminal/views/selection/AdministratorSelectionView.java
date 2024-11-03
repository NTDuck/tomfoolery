package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.infrastructures.adapters.selection.SelectionAdapter;
import org.tomfoolery.configurations.monolith.terminal.infrastructures.dataproviders.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.action.admin.auth.CreateStaffAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.admin.auth.DeleteStaffAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.admin.auth.UpdateStaffCredentialsActionView;

import java.util.List;

public class AdministratorSelectionView extends UserSelectionView {
    public static @NonNull AdministratorSelectionView of(@NonNull IOHandler ioHandler) {
        return new AdministratorSelectionView(ioHandler);
    }

    private AdministratorSelectionView(@NonNull IOHandler ioHandler) {
        super(ioHandler, List.of(
            SelectionAdapter.Item.of(7, "Create Staff account", CreateStaffAccountActionView.class),
            SelectionAdapter.Item.of(8, "Update Staff credentials", UpdateStaffCredentialsActionView.class),
            SelectionAdapter.Item.of(9, "Delete Staff account", DeleteStaffAccountActionView.class)
        ));
    }

    @Override
    protected @NonNull String getPrompt() {
        return "Hello admin!";
    }
}
