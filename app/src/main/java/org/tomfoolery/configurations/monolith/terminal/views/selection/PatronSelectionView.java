package org.tomfoolery.configurations.monolith.terminal.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.selection.abc.UserSelectionView;

import java.util.List;

public class PatronSelectionView extends UserSelectionView {
    public static @NonNull PatronSelectionView of(@NonNull IOHandler ioHandler) {
        return new PatronSelectionView(ioHandler);
    }

    private PatronSelectionView(@NonNull IOHandler ioHandler) {
        super(ioHandler, List.of(

        ));
    }

    @Override
    protected @NonNull String getPrompt() {
        return "Hello patron!";
    }
}
