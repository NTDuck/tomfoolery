package org.tomfoolery.configurations.monolith.console.views.action.common.documents.retrieval;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.HorizontalAlign;
import com.github.freva.asciitable.OverflowBehaviour;
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
import org.tomfoolery.core.usecases.external.common.documents.retrieval.ShowDocumentsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.common.documents.retrieval.GetDocumentByIdController;
import org.tomfoolery.infrastructures.adapters.controllers.external.common.documents.retrieval.ShowDocumentsController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;

import java.util.List;

public final class ShowDocumentsActionView extends UserActionView {
    private final @NonNull ShowDocumentsController showDocumentsController;

    public static @NonNull ShowDocumentsActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        return new ShowDocumentsActionView(ioProvider, documentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileStorageProvider);
    }

    private ShowDocumentsActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        super(ioProvider);

        this.showDocumentsController = ShowDocumentsController.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileStorageProvider);
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

        val table = AsciiTable.builder()
            .border(AsciiTable.NO_BORDERS)
            .data(viewModel.getPaginatedDocuments(), List.of(
                new Column()
                    .header("ISBN 10")
                    .headerAlign(HorizontalAlign.CENTER)
                    .with(GetDocumentByIdController.ViewModel::getDocumentISBN_10),
                new Column()
                    .header("ISBN 13")
                    .headerAlign(HorizontalAlign.CENTER)
                    .with(GetDocumentByIdController.ViewModel::getDocumentISBN_13),
                new Column()
                    .header("Title")
                    .headerAlign(HorizontalAlign.CENTER)
                    .maxWidth(30, OverflowBehaviour.CLIP_RIGHT)
                    .dataAlign(HorizontalAlign.LEFT)
                    .with(GetDocumentByIdController.ViewModel::getDocumentTitle),
                new Column()
                    .header("Authors")
                    .headerAlign(HorizontalAlign.CENTER)
                    .maxWidth(14, OverflowBehaviour.CLIP_RIGHT)
                    .dataAlign(HorizontalAlign.LEFT)
                    .with(document -> String.join(", ", document.getDocumentAuthors())),
                new Column()
                    .header("Genres")
                    .headerAlign(HorizontalAlign.CENTER)
                    .maxWidth(14, OverflowBehaviour.CLIP_RIGHT)
                    .dataAlign(HorizontalAlign.LEFT)
                    .with(document -> String.join(", ", document.getDocumentGenres())),
                new Column()
                    .header("Year")
                    .headerAlign(HorizontalAlign.CENTER)
                    .dataAlign(HorizontalAlign.RIGHT)
                    .with(document -> String.valueOf(document.getDocumentPublishedYear()))
            ))
            .asString();

        this.ioProvider.writeLine(table);
    }

    private void onSuccess(ShowDocumentsController.@NonNull ViewModel viewModel) {
        this.nextViewClass = cachedViewClass;

        this.displayViewModel(viewModel);
    }

    private static class PageIndexInvalidException extends Exception {}
}
