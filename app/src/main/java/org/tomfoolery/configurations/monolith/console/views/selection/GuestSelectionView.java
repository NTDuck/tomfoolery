package org.tomfoolery.configurations.monolith.console.views.selection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.views.selection.abc.BaseSelectionView;
import org.tomfoolery.configurations.monolith.console.views.action.guest.users.persistence.CreatePatronAccountActionView;
import org.tomfoolery.configurations.monolith.console.views.action.guest.users.authentication.LogUserInByCredentialsActionView;

import java.util.List;

public final class GuestSelectionView extends BaseSelectionView {
    public static @NonNull GuestSelectionView of(@NonNull IOProvider ioProvider) {
        return new GuestSelectionView(ioProvider);
    }

    private GuestSelectionView(@NonNull IOProvider ioProvider) {
        super(ioProvider, List.of(
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
