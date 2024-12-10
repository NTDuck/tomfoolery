package org.tomfoolery.configurations.monolith.console.views.action.staff.documents.retrieval;

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
import org.tomfoolery.configurations.monolith.console.views.selection.StaffSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.staff.documents.retrieval.ShowDocumentsWithoutContentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.common.documents.retrieval.GetDocumentByIdController;
import org.tomfoolery.infrastructures.adapters.controllers.staff.documents.retrieval.ShowDocumentsWithoutContentController;

import java.util.List;

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

        val table = AsciiTable.builder()
            .border(AsciiTable.NO_BORDERS)
            .data(viewModel.getPaginatedDocumentsWithoutContent(), List.of(
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

    private void onSuccess(ShowDocumentsWithoutContentController.@NonNull ViewModel viewModel) {
        this.nextViewClass = StaffSelectionView.class;

        this.displayViewModel(viewModel);
    }

    private static class PageIndexInvalidException extends Exception {}
}
