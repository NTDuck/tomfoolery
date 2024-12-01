package org.tomfoolery.configurations.monolith.console.views.action.user.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.shared.documents.ShowDocumentsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.user.documents.ShowDocumentsController;

public final class ShowDocumentsActionView extends UserActionView {
    private static final @Unsigned int MAX_PAGE_SIZE = 7;

    private final @NonNull ShowDocumentsController controller;

    public static @NonNull ShowDocumentsActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ShowDocumentsActionView(ioProvider, documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ShowDocumentsActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.controller = ShowDocumentsController.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
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
        val pageIndex = this.collectPageIndex();

        return ShowDocumentsController.RequestObject.of(pageIndex, MAX_PAGE_SIZE);
    }

    private @Unsigned int collectPageIndex() throws PageIndexInvalidException {
        val rawPageIndex = this.ioProvider.readLine(Message.Format.PROMPT, "page number");

        try {
            return Integer.parseUnsignedInt(rawPageIndex);

        } catch (NumberFormatException exception) {
            throw new PageIndexInvalidException();
        }
    }

    private void onSuccess(ShowDocumentsController.@NonNull ViewModel viewModel) {
        this.nextViewClass = cachedViewClass;
        this.displayViewModel(viewModel);
    }

    private void displayViewModel(ShowDocumentsController.@NonNull ViewModel viewModel) {
        val pageIndex = viewModel.getPageIndex();
        val maxPageIndex = viewModel.getMaxPageIndex();

        this.ioProvider.writeLine("Showing documents, page %d of %d", pageIndex, maxPageIndex);

        viewModel.getPaginatedFragmentaryDocuments()
            .forEach(fragmentaryDocument -> {
                val ISBN = fragmentaryDocument.getISBN();
                val documentTitle = fragmentaryDocument.getDocumentTitle();

                this.ioProvider.writeLine("- (%s) %s", ISBN, documentTitle);
            });
    }

    private void onPageIndexInvalidException() {
        this.nextViewClass = cachedViewClass;

        this.ioProvider.writeLine(Message.Format.ERROR, "Page number must be a positive integer");
    }

    private void onPaginationInvalidException() {
        this.nextViewClass = cachedViewClass;

        this.ioProvider.writeLine(Message.Format.ERROR, "Found no documents with such page number");
    }

    private static class PageIndexInvalidException extends Exception {}
}
