package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.selection.abc.UserSelectionView;

import java.util.List;

public class AdministratorSelectionView extends UserSelectionView {
    public static @NonNull AdministratorSelectionView of(@NonNull IOHandler ioHandler) {
        return new AdministratorSelectionView(ioHandler);
    }

    private AdministratorSelectionView(@NonNull IOHandler ioHandler) {
        super(ioHandler, List.of(

        ));
    }

    @Override
    protected @NonNull String getPrompt() {
        return "Hello admin!";
    }
}
