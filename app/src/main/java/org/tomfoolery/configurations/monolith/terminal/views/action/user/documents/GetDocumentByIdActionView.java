package org.tomfoolery.configurations.monolith.terminal.views.action.user.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.SelectionViewResolver;
import org.tomfoolery.configurations.monolith.terminal.views.action.abc.UserActionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.user.documents.GetDocumentByIdUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.user.documents.GetDocumentByIdController;

public final class GetDocumentByIdActionView extends UserActionView {
    private final @NonNull GetDocumentByIdController controller;

    private final @NonNull SelectionViewResolver selectionViewResolver;

    public static @NonNull GetDocumentByIdActionView of(@NonNull IOHandler ioHandler, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetDocumentByIdActionView(ioHandler, documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetDocumentByIdActionView(@NonNull IOHandler ioHandler, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioHandler);

        this.controller = GetDocumentByIdController.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
        this.selectionViewResolver = SelectionViewResolver.of(authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            val viewModel = this.controller.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (GetDocumentByIdUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (GetDocumentByIdUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (GetDocumentByIdUseCase.DocumentNotFoundException exception) {
            this.onDocumentNotFoundException();
        }
    }

    private GetDocumentByIdController.@NonNull RequestObject collectRequestObject() {
        val ISBN = this.ioHandler.readLine(Message.Format.PROMPT, "document ISBN");

        return GetDocumentByIdController.RequestObject.of(ISBN);
    }

    private void displayViewModel(GetDocumentByIdController.@NonNull ViewModel viewModel) {
        val fragmentaryDocument = viewModel.getFragmentaryDocument();

        this.ioHandler.writeLine("Here is your document:");

        this.ioHandler.writeLine("ISBN: %s", fragmentaryDocument.getISBN());

        this.ioHandler.writeLine("Title: %s", fragmentaryDocument.getDocumentTitle());
        this.ioHandler.writeLine("Description: %s", fragmentaryDocument.getDocumentDescription());
        this.ioHandler.writeLine("Authors: %s", String.join(", ", fragmentaryDocument.getDocumentAuthors()));
        this.ioHandler.writeLine("Genres: %s", String.join(", ", fragmentaryDocument.getDocumentGenres()));

        this.ioHandler.writeLine("Published year: %d", fragmentaryDocument.getDocumentPublishedYear());
        this.ioHandler.writeLine("Publisher: %s", fragmentaryDocument.getDocumentPublisher());

        this.ioHandler.writeLine("Rating: %f (%d rated, %d currently borrowing)", fragmentaryDocument.getAverageRating(), fragmentaryDocument.getNumberOfRatings(), fragmentaryDocument.getNumberOfBorrowingPatrons());
    }

    private void onSuccess(GetDocumentByIdController.@NonNull ViewModel viewModel) {
        this.nextViewClass = this.selectionViewResolver.getMostRecentSelectionView();
        this.displayViewModel(viewModel);
    }

    private void onDocumentNotFoundException() {
        this.nextViewClass = this.selectionViewResolver.getMostRecentSelectionView();

        this.ioHandler.writeLine(Message.Format.ERROR, "Document not found");
    }
}
