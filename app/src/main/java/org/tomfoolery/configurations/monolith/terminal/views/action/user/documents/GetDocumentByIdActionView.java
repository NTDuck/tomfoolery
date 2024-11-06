package org.tomfoolery.configurations.monolith.terminal.views.action.user.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.abc.BaseView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.abc.SharedActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.usecases.user.documents.GetDocumentByIdUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.user.documents.GetDocumentByIdController;
import org.tomfoolery.infrastructures.adapters.presenters.user.documents.GetDocumentByIdPresenter;

public final class GetDocumentByIdActionView extends SharedActionView {
    private final @NonNull GetDocumentByIdController controller;
    private final @NonNull GetDocumentByIdPresenter presenter;

    private @NonNull Class<? extends BaseView> nextViewClass = GuestSelectionView.class;

    public static @NonNull GetDocumentByIdActionView of(@NonNull IOHandler ioHandler, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        return new GetDocumentByIdActionView(ioHandler, authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    private GetDocumentByIdActionView(@NonNull IOHandler ioHandler, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        super(ioHandler, authenticationTokenGenerator, authenticationTokenRepository);

        this.controller = GetDocumentByIdController.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
        this.presenter = GetDocumentByIdPresenter.of();
    }

    @Override
    public void run() {
        val requestObject = collectRequestObject();

        try {
            val responseModel = this.controller.apply(requestObject);
            val viewModel = this.presenter.apply(responseModel);

            onSuccess(viewModel);

        } catch (AuthenticatedUserUseCase.AuthenticationTokenInvalidException exception) {
            throw new RuntimeException(e);
        } catch (AuthenticatedUserUseCase.AuthenticationTokenNotFoundException exception) {
            throw new RuntimeException(e);
        } catch (GetDocumentByIdUseCase.DocumentNotFoundException exception) {
            throw new RuntimeException(e);
        }
    }

    private GetDocumentByIdController.@NonNull Request collectRequestObject() {
        val ISBN = this.ioHandler.readLine(PROMPT_MESSAGE_FORMAT, "ISBN");
        return GetDocumentByIdController.Request.of(ISBN);
    }

    private void onSuccess(GetDocumentByIdPresenter.@NonNull ViewModel viewModel) {
        this.nextViewClass = super.getNextViewClass();
        displayViewModel(viewModel);
    }

    private void onAuthenticationTokenInvalidException() {
        this.nextViewClass = GuestSelectionView.class;
        displayViewModel();
    }

    private void displayViewModel(GetDocumentByIdPresenter.@NonNull ViewModel viewModel) {
        this.ioHandler.writeLine("ISBN: %s", viewModel.getISBN());

        this.ioHandler.writeLine("Title: %s", viewModel.getTitle());
        this.ioHandler.writeLine("Description: %s", viewModel.getDescription());
        this.ioHandler.writeLine("Authors: %s", String.join(", ", viewModel.getAuthors()));
        this.ioHandler.writeLine("Genres: %s", String.join(", ", viewModel.getGenres()));

        this.ioHandler.writeLine("Published year: %d", viewModel.getPublishedYear());
        this.ioHandler.writeLine("Publisher: %s", viewModel.getPublisher());

        this.ioHandler.writeLine("Rating: %f (%d rated, %d currently borrowing)", viewModel.getRating(), viewModel.getRatingCount(), viewModel.getBorrowingPatronCount());
        this.ioHandler.writeLine("There's also a cover image which is not displayed in console env");
    }
}
