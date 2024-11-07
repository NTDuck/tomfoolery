package org.tomfoolery.configurations.monolith.terminal.views.action.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.abc.BaseView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.infrastructures.utils.dataclasses.ViewonlyDocumentPreview;

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

    protected void displayViewonlyDocumentPreview(@NonNull ViewonlyDocumentPreview viewonlyDocumentPreview) {
        this.ioHandler.writeLine("ISBN: %s", viewonlyDocumentPreview.getISBN());

        this.ioHandler.writeLine("Title: %s", viewonlyDocumentPreview.getTitle());
        this.ioHandler.writeLine("Description: %s", viewonlyDocumentPreview.getDescription());
        this.ioHandler.writeLine("Authors: %s", String.join(", ", viewonlyDocumentPreview.getAuthors()));
        this.ioHandler.writeLine("Genres: %s", String.join(", ", viewonlyDocumentPreview.getGenres()));

        this.ioHandler.writeLine("Published year: %d", viewonlyDocumentPreview.getPublishedYear());
        this.ioHandler.writeLine("Publisher: %s", viewonlyDocumentPreview.getPublisher());

        this.ioHandler.writeLine("Rating: %f (%d rated, %d currently borrowing)", viewonlyDocumentPreview.getRating(), viewonlyDocumentPreview.getRatingCount(), viewonlyDocumentPreview.getBorrowingPatronCount());
    }
}
