package org.tomfoolery.configurations.monolith.terminal.views.action.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.abc.BaseView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;

public abstract class UserActionView extends BaseView {
    protected UserActionView(@NonNull IOHandler ioHandler) {
        super(ioHandler);
    }

    protected void onAuthenticationTokenNotFoundException() {
        this.nextViewClass = GuestSelectionView.class;
        this.ioHandler.writeLine(ERROR_MESSAGE_FORMAT, "Something wrong happened, please log in again");
    }

    protected void onAuthenticationTokenInvalidException() {
        this.nextViewClass = GuestSelectionView.class;
        this.ioHandler.writeLine(ERROR_MESSAGE_FORMAT, "Your session has expired, please log in again");
    }

    protected void displayViewonlyDocumentPreview(@NonNull ViewableDocumentPreview viewableDocumentPreview) {
        this.ioHandler.writeLine("ISBN: %s", viewableDocumentPreview.getISBN());

        this.ioHandler.writeLine("Title: %s", viewableDocumentPreview.getTitle());
        this.ioHandler.writeLine("Description: %s", viewableDocumentPreview.getDescription());
        this.ioHandler.writeLine("Authors: %s", String.join(", ", viewableDocumentPreview.getAuthors()));
        this.ioHandler.writeLine("Genres: %s", String.join(", ", viewableDocumentPreview.getGenres()));

        this.ioHandler.writeLine("Published year: %d", viewableDocumentPreview.getPublishedYear());
        this.ioHandler.writeLine("Publisher: %s", viewableDocumentPreview.getPublisher());

        this.ioHandler.writeLine("Rating: %f (%d rated, %d currently borrowing)", viewableDocumentPreview.getRating(), viewableDocumentPreview.getRatingCount(), viewableDocumentPreview.getBorrowingPatronCount());
    }
}
