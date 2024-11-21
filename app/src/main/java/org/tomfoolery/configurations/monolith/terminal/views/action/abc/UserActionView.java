package org.tomfoolery.configurations.monolith.terminal.views.action.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.views.abc.BaseView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.infrastructures.utils.dataclasses.ViewableFragmentaryDocument;

public abstract class UserActionView extends BaseView {
    protected UserActionView(@NonNull IOHandler ioHandler) {
        super(ioHandler);
    }

    protected void onAuthenticationTokenNotFoundException() {
        this.nextViewClass = GuestSelectionView.class;

        this.ioHandler.writeLine(Message.Format.ERROR, "Something wrong happened, please log in again");
    }

    protected void onAuthenticationTokenInvalidException() {
        this.nextViewClass = GuestSelectionView.class;
        
        this.ioHandler.writeLine(Message.Format.ERROR, "Your session has expired, please log in again");
    }
}
