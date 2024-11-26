package org.tomfoolery.configurations.monolith.terminal.views.action.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.views.abc.BaseView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;

public abstract class UserActionView extends BaseView {
    protected UserActionView(@NonNull IOProvider ioProvider) {
        super(ioProvider);
    }

    protected void onAuthenticationTokenNotFoundException() {
        this.nextViewClass = GuestSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Something wrong happened, please log in again");
    }

    protected void onAuthenticationTokenInvalidException() {
        this.nextViewClass = GuestSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Your session has expired, please log in again");
    }
}
