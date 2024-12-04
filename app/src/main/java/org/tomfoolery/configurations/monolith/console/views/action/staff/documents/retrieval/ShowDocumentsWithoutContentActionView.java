package org.tomfoolery.configurations.monolith.console.views.action.staff.documents.retrieval;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.StaffSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.staff.documents.retrieval.ShowDocumentsWithoutContentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.staff.documents.retrieval.ShowDocumentsWithoutContentController;

public final class ShowDocumentsWithoutContentActionView extends UserActionView {
    private final @NonNull ShowDocumentsWithoutContentController showDocumentsWithoutContentController;

    public static @NonNull ShowDocumentsWithoutContentActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ShowDocumentsWithoutContentActionView(ioProvider, documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ShowDocumentsWithoutContentActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.showDocumentsWithoutContentController = ShowDocumentsWithoutContentController.of(documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            val viewModel = this.showDocumentsWithoutContentController.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (ShowDocumentsWithoutContentUseCase.AuthenticationTokenNotFoundException | ShowDocumentsWithoutContentUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (PageIndexInvalidException | ShowDocumentsWithoutContentUseCase.PaginationInvalidException exception) {
            this.onException(exception);
        }
    }

    private ShowDocumentsWithoutContentController.@NonNull RequestObject collectRequestObject() throws PageIndexInvalidException {
        val pageIndex = this.collectPageIndex();

        return ShowDocumentsWithoutContentController.RequestObject.of(pageIndex, Message.Page.MAX_PAGE_SIZE);
    }

    private @Unsigned int collectPageIndex() throws PageIndexInvalidException {
        val rawPageIndex = this.ioProvider.readLine(Message.Format.PROMPT, "page number");

        try {
            return Integer.parseUnsignedInt(rawPageIndex);

        } catch (NumberFormatException exception) {
            throw new PageIndexInvalidException();
        }
    }

    private void displayViewModel(ShowDocumentsWithoutContentController.@NonNull ViewModel viewModel) {
        this.ioProvider.writeLine("Showing documents with missing content, page %d of %d", viewModel.getPageIndex(), viewModel.getMaxPageIndex());

        viewModel.getPaginatedDocumentsWithoutContent()
            .forEach(document -> {
                this.ioProvider.writeLine("- [%s] %s", document.getDocumentISBN_10(), document.getDocumentTitle());
            });
    }

    private void onSuccess(ShowDocumentsWithoutContentController.@NonNull ViewModel viewModel) {
        this.nextViewClass = StaffSelectionView.class;

        this.displayViewModel(viewModel);
    }

    private static class PageIndexInvalidException extends Exception {}
}
