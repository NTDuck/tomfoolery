package org.tomfoolery.configurations.monolith.console.views.action.patron.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.PatronSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.documents.borrow.ShowBorrowedDocumentsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.ShowBorrowedDocumentsController;

public final class ShowBorrowedDocumentsActionView extends UserActionView {
    private static final @Unsigned int MAX_PAGE_SIZE = 5;

    private final @NonNull ShowBorrowedDocumentsController controller;

    public static @NonNull ShowBorrowedDocumentsActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ShowBorrowedDocumentsActionView(ioProvider, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ShowBorrowedDocumentsActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.controller = ShowBorrowedDocumentsController.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            val viewModel = this.controller.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (PageIndexInvalidException exception) {
            this.onPageIndexInvalidException();

        } catch (ShowBorrowedDocumentsUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (ShowBorrowedDocumentsUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (ShowBorrowedDocumentsUseCase.PatronNotFoundException exception) {
            this.onPatronNotFoundException();
        } catch (ShowBorrowedDocumentsUseCase.PaginationInvalidException exception) {
            this.onPaginationInvalidException();
        }
    }

    private ShowBorrowedDocumentsController.@NonNull RequestObject collectRequestObject() throws PageIndexInvalidException {
        val pageIndex = this.collectPageIndex();

        return ShowBorrowedDocumentsController.RequestObject.of(pageIndex, MAX_PAGE_SIZE);
    }

    private void onSuccess(ShowBorrowedDocumentsController.@NonNull ViewModel viewModel) {
        this.nextViewClass = PatronSelectionView.class;
        this.displayViewModel(viewModel);
    }

    private @Unsigned int collectPageIndex() throws PageIndexInvalidException {
        val rawPageIndex = this.ioProvider.readLine(Message.Format.PROMPT, "page number");

        try {
            return Integer.parseUnsignedInt(rawPageIndex);

        } catch (NumberFormatException exception) {
            throw new PageIndexInvalidException();
        }
    }

    private void displayViewModel(ShowBorrowedDocumentsController.@NonNull ViewModel viewModel) {
        val pageIndex = viewModel.getPageIndex();
        val maxPageIndex = viewModel.getMaxPageIndex();

        this.ioProvider.writeLine("Showing borrowed documents, page %d of %d", pageIndex, maxPageIndex);

        viewModel.getPaginatedFragmentaryDocuments()
            .forEach(fragmentaryDocument -> {
                val ISBN = fragmentaryDocument.getISBN();
                val documentTitle = fragmentaryDocument.getDocumentTitle();

                this.ioProvider.writeLine("- (%s) %s", ISBN, documentTitle);
            });
    }

    private void onPageIndexInvalidException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Page number must be a positive integer");
    }

    private void onPatronNotFoundException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Patron not found");
    }

    private void onPaginationInvalidException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Found no documents with such page number");
    }

    private static class PageIndexInvalidException extends Exception {}
}
