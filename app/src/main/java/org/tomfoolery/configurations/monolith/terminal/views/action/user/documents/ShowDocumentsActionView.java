package org.tomfoolery.configurations.monolith.terminal.views.action.user.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.SelectionViewResolver;
import org.tomfoolery.configurations.monolith.terminal.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.user.documents.ShowDocumentsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.user.documents.ShowDocumentsController;

public final class ShowDocumentsActionView extends UserActionView {
    private static final @Unsigned int MAX_PAGE_SIZE = 7;

    private final @NonNull ShowDocumentsController controller;

    private final @NonNull SelectionViewResolver selectionViewResolver;

    public static @NonNull ShowDocumentsActionView of(@NonNull IOHandler ioHandler, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ShowDocumentsActionView(ioHandler, documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ShowDocumentsActionView(@NonNull IOHandler ioHandler, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioHandler);

        this.controller = ShowDocumentsController.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
        this.selectionViewResolver = SelectionViewResolver.of(authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            val viewModel = this.controller.apply(requestObject);

            this.onSuccess(viewModel);

        } catch (PageIndexInvalidException exception) {
            this.onPageIndexInvalidException();

        } catch (ShowDocumentsUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (ShowDocumentsUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (ShowDocumentsUseCase.PaginationInvalidException exception) {
            this.onPaginationInvalidException();
        }
    }

    private ShowDocumentsController.@NonNull RequestObject collectRequestObject() throws PageIndexInvalidException {
        val rawPageIndex = this.ioHandler.readLine(Message.Format.PROMPT, "page number");

        try {
            val pageIndex = Integer.parseUnsignedInt(rawPageIndex);
            return ShowDocumentsController.RequestObject.of(pageIndex, MAX_PAGE_SIZE);

        } catch (NumberFormatException exception) {
            throw new PageIndexInvalidException();
        }
    }

    private void onSuccess(ShowDocumentsController.@NonNull ViewModel viewModel) {
        this.nextViewClass = this.selectionViewResolver.getMostRecentSelectionView();
        this.displayViewModel(viewModel);
    }

    private void displayViewModel(ShowDocumentsController.@NonNull ViewModel viewModel) {
        val pageIndex = viewModel.getPageIndex();
        val maxPageIndex = viewModel.getMaxPageIndex();

        this.ioHandler.writeLine("Showing documents, page %d of %d", pageIndex, maxPageIndex);

        viewModel.getPaginatedFragmentaryDocuments()
            .forEach(fragmentaryDocument -> {
                val ISBN = fragmentaryDocument.getISBN();
                val documentTitle = fragmentaryDocument.getDocumentTitle();

                this.ioHandler.writeLine("- (%s) %s", ISBN, documentTitle);
            });
    }

    private void onPageIndexInvalidException() {
        this.nextViewClass = this.selectionViewResolver.getMostRecentSelectionView();

        this.ioHandler.writeLine(Message.Format.ERROR, "Page number must be a positive integer");
    }

    private void onPaginationInvalidException() {
        this.nextViewClass = this.selectionViewResolver.getMostRecentSelectionView();

        this.ioHandler.writeLine(Message.Format.ERROR, "Found no documents with such page number");
    }

    private static class PageIndexInvalidException extends Exception {}
}
