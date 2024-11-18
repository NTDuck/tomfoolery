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

    protected void displayViewableFragmentaryDocument(@NonNull ViewableFragmentaryDocument viewableFragmentaryDocument) {
        this.ioHandler.writeLine("ISBN: %s", viewableFragmentaryDocument.getISBN());

        this.ioHandler.writeLine("Title: %s", viewableFragmentaryDocument.getDocumentTitle());
        this.ioHandler.writeLine("Description: %s", viewableFragmentaryDocument.getDocumentDescription());
        this.ioHandler.writeLine("Authors: %s", String.join(", ", viewableFragmentaryDocument.getDocumentAuthors()));
        this.ioHandler.writeLine("Genres: %s", String.join(", ", viewableFragmentaryDocument.getDocumentGenres()));

        this.ioHandler.writeLine("Published year: %d", viewableFragmentaryDocument.getDocumentPublishedYear());
        this.ioHandler.writeLine("Publisher: %s", viewableFragmentaryDocument.getDocumentPublisher());

        this.ioHandler.writeLine("Rating: %f (%d rated, %d currently borrowing)", viewableFragmentaryDocument.getAverageRating(), viewableFragmentaryDocument.getNumberOfRatings(), viewableFragmentaryDocument.getNumberOfBorrowingPatrons());
    }
}
