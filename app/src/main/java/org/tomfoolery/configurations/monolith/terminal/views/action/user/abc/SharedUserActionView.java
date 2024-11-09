package org.tomfoolery.configurations.monolith.terminal.views.action.user.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.adapters.action.user.SharedActionAdapter;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.abc.UserSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;

public abstract class SharedUserActionView extends UserActionView {
    private final @NonNull SharedActionAdapter adapter;

    protected SharedUserActionView(@NonNull IOHandler ioHandler, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioHandler);
        this.adapter = SharedActionAdapter.of(authenticationTokenGenerator, authenticationTokenRepository);
    }

    protected @NonNull Class<? extends UserSelectionView> getCurrentUserSelectionViewClassFromUserClass() {
        val viewModel = this.adapter.get();
        val userClass = viewModel.getUserClass();

        return UserSelectionView.getUserSelectionViewClassFromUserClass(userClass);
    }
}
