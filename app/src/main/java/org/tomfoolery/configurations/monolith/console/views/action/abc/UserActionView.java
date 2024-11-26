package org.tomfoolery.configurations.monolith.console.views.action.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;

public abstract class UserActionView extends BaseActionView {
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
