package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.action.admin.auth.CreateStaffAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.admin.auth.DeleteStaffAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.admin.auth.UpdateStaffCredentialsActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.abc.UserSelectionView;

import java.util.List;

public class AdministratorSelectionView extends UserSelectionView {
    public static @NonNull AdministratorSelectionView of(@NonNull IOHandler ioHandler) {
        return new AdministratorSelectionView(ioHandler);
    }

    private AdministratorSelectionView(@NonNull IOHandler ioHandler) {
        super(ioHandler, List.of(
            SelectionItem.of(7, "Create Staff account", CreateStaffAccountActionView.class),
            SelectionItem.of(8, "Update Staff credentials", UpdateStaffCredentialsActionView.class),
            SelectionItem.of(9, "Delete Staff account", DeleteStaffAccountActionView.class)
        ));
    }

    @Override
    protected @NonNull String getPrompt() {
        return String.format("%s, %s", super.getPrompt(), "you are an Administrator!");
    }
}
