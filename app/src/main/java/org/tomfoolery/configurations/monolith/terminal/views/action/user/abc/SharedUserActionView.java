package org.tomfoolery.configurations.monolith.terminal.views.action.user.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.adapters.controllers.action.user.SharedUserActionPresenter;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.action.abc.UserActionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;

public abstract class SharedUserActionView extends UserActionView {
    private final @NonNull SharedUserActionPresenter presenter;

    protected SharedUserActionView(@NonNull IOHandler ioHandler, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioHandler);
        
        this.presenter = SharedUserActionPresenter.of(authenticationTokenGenerator, authenticationTokenRepository);
    }
}
