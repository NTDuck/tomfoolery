package org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.references;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.action.abc.ActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.abc.SharedUserActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.abc.BaseSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;

public class GetDocumentQrCodeActionView extends SharedUserActionView {
    protected GetDocumentQrCodeActionView(@NonNull IOHandler ioHandler, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioHandler, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {

    }

    @Override
    public @NonNull Class<? extends BaseSelectionView> getNextViewClass() {
        return null;
    }
}
