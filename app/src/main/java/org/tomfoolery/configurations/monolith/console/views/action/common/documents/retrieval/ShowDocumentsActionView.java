package org.tomfoolery.configurations.monolith.console.views.action.common.documents.retrieval;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.common.documents.retrieval.ShowDocumentsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.common.documents.retrieval.ShowDocumentsController;

public final class ShowDocumentsActionView extends UserActionView {
    private final @NonNull ShowDocumentsController showDocumentsController;

    public static @NonNull ShowDocumentsActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ShowDocumentsActionView(ioProvider, documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ShowDocumentsActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.showDocumentsController = ShowDocumentsController.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            val viewModel = this.showDocumentsController.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (ShowDocumentsUseCase.AuthenticationTokenNotFoundException | ShowDocumentsUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (PageIndexInvalidException | ShowDocumentsUseCase.PaginationInvalidException exception) {
            this.onException(exception);
        }
    }

    private ShowDocumentsController.@NonNull RequestObject collectRequestObject() throws PageIndexInvalidException {
        val pageIndex = this.collectPageIndex();

        return ShowDocumentsController.RequestObject.of(pageIndex, Message.Page.MAX_PAGE_SIZE);
    }

    private @Unsigned int collectPageIndex() throws PageIndexInvalidException {
        val rawPageIndex = this.ioProvider.readLine(Message.Format.PROMPT, "page number");

        try {
            return Integer.parseUnsignedInt(rawPageIndex);

        } catch (NumberFormatException exception) {
            throw new PageIndexInvalidException();
        }
    }

    private void displayViewModel(ShowDocumentsController.@NonNull ViewModel viewModel) {
        this.ioProvider.writeLine("Showing documents, page %d of %d", viewModel.getPageIndex(), viewModel.getMaxPageIndex());

        viewModel.getPaginatedDocuments()
            .forEach(document -> {
                this.ioProvider.writeLine("- [%s] %s", document.getDocumentISBN_10(), document.getDocumentTitle());
            });
    }

    private void onSuccess(ShowDocumentsController.@NonNull ViewModel viewModel) {
        this.nextViewClass = cachedViewClass;

        this.displayViewModel(viewModel);
    }

    private static class PageIndexInvalidException extends Exception {}
}
