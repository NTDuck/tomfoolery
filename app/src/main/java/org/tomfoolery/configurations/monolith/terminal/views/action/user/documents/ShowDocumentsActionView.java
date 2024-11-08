package org.tomfoolery.configurations.monolith.terminal.views.action.user.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.abc.SharedUserActionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.usecases.user.documents.ShowDocumentsUseCase;
import org.tomfoolery.infrastructures.adapters.presenters.user.documents.ShowDocumentsPresenter;

public final class ShowDocumentsActionView extends SharedUserActionView {
    private final @NonNull ShowDocumentsUseCase useCase;
    private final @NonNull ShowDocumentsPresenter presenter;

    private ShowDocumentsActionView(@NonNull IOHandler ioHandler, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        super(ioHandler, authenticationTokenGenerator, authenticationTokenRepository);

        this.useCase = ShowDocumentsUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
        this.presenter = ShowDocumentsPresenter.of();
    }

    @Override
    public void run() {
        try {
            val requestModel = collectRequestModel();
            val responseModel = this.useCase.apply(requestModel);
            val viewModel = this.presenter.apply(responseModel);

            onSuccess(viewModel);

        } catch (PageIndexInvalidException exception) {
            onPageIndexInvalidException();
        } catch (AuthenticatedUserUseCase.AuthenticationTokenNotFoundException e) {
            onAuthenticationTokenNotFoundException();
        } catch (AuthenticatedUserUseCase.AuthenticationTokenInvalidException e) {
            onAuthenticationTokenInvalidException();
        } catch (ShowDocumentsUseCase.PaginationInvalidException e) {
            onDocumentsNotFoundException();
        }
    }

    private ShowDocumentsUseCase.@NonNull Request collectRequestModel() throws PageIndexInvalidException {
        val pageIndexAsString = this.ioHandler.readLine(PROMPT_MESSAGE_FORMAT, "page number");

        try {
            val pageIndex = Integer.parseUnsignedInt(pageIndexAsString);
            return ShowDocumentsUseCase.Request.of(pageIndex, MAX_PAGE_SIZE);
        } catch (NumberFormatException exception) {
            throw new PageIndexInvalidException();
        }
    }

    private void onSuccess(ShowDocumentsPresenter.@NonNull ViewModel viewModel) {
        this.nextViewClass = getCurrentUserSelectionViewClassFromUserClass();
        displayViewModel(viewModel);
    }

    private void onPageIndexInvalidException() {
        this.nextViewClass = getCurrentUserSelectionViewClassFromUserClass();
        this.ioHandler.writeLine(ERROR_MESSAGE_FORMAT, "Invalid page index");
    }

    private void onDocumentsNotFoundException() {
        this.nextViewClass = getCurrentUserSelectionViewClassFromUserClass();
        this.ioHandler.writeLine(ERROR_MESSAGE_FORMAT, "Documents not found");
    }

    private void displayViewModel(ShowDocumentsPresenter.@NonNull ViewModel viewModel) {
        this.ioHandler.writeLine("Page %d of %d", viewModel.getPageIndex(), viewModel.getMaxPageIndex());

        for (val viewonlyDocumentPreview : viewModel.getViewableDocumentPreviews()) {
            displayViewonlyDocumentPreview(viewonlyDocumentPreview);
            this.ioHandler.writeLine("");
        }

        this.ioHandler.writeLine("There's also cover images which are not displayed in console env");
    }

    private static class PageIndexInvalidException extends Exception {}
}
