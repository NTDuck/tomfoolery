package org.tomfoolery.configurations.monolith.terminal.views.action.user.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.abc.SharedUserActionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.usecases.user.documents.GetDocumentByIdUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.user.documents.GetDocumentByIdController;
import org.tomfoolery.infrastructures.adapters.presenters.user.documents.GetDocumentByIdPresenter;

public final class GetDocumentByIdActionView extends SharedUserActionView {
    private final @NonNull GetDocumentByIdController controller;
    private final @NonNull GetDocumentByIdPresenter presenter;

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
            onAuthenticationTokenInvalidException();
        } catch (AuthenticatedUserUseCase.AuthenticationTokenNotFoundException exception) {
            onAuthenticationTokenNotFoundException();
        } catch (GetDocumentByIdUseCase.DocumentNotFoundException exception) {
            onDocumentNotFoundException();
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

    private void onDocumentNotFoundException() {
        this.nextViewClass = getCurrentUserSelectionViewClassFromUserClass();
        this.ioHandler.writeLine(ERROR_MESSAGE_FORMAT, "Document not found");
    }

    private void displayViewModel(GetDocumentByIdPresenter.@NonNull ViewModel viewModel) {
        val viewonlyDocumentPreview = viewModel.getViewonlyDocumentPreview();

        this.ioHandler.writeLine("Here is your document:");
        displayViewonlyDocumentPreview(viewonlyDocumentPreview);
        this.ioHandler.writeLine("There's also a cover image which is not displayed in console env");
    }
}
