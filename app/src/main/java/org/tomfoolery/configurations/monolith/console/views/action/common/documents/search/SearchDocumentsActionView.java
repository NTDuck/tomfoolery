package org.tomfoolery.configurations.monolith.console.views.action.common.documents.search;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.HorizontalAlign;
import com.github.freva.asciitable.OverflowBehaviour;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.utils.helpers.EnumResolver;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.common.documents.search.abc.SearchDocumentsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.common.documents.retrieval.GetDocumentByIdController;
import org.tomfoolery.infrastructures.adapters.controllers.external.common.documents.search.SearchDocumentsController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;

import java.util.List;
import java.util.stream.Collectors;

public final class SearchDocumentsActionView extends UserActionView {
    private static final @NonNull String SEARCH_CRITERION_PROMPT = String.format("search criterion (%s)", EnumResolver.getCapitalizedEnumeratedNames(
        SearchDocumentsController.SearchCriterion.class, "%s (%d)", 0
    ).stream().collect(Collectors.joining(", ")));

    private final @NonNull SearchDocumentsController controller;

    public static @NonNull SearchDocumentsActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        return new SearchDocumentsActionView(ioProvider, documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository, fileStorageProvider);
    }

    private SearchDocumentsActionView(@NonNull IOProvider ioProvider, @NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        super(ioProvider);

        this.controller = SearchDocumentsController.of(documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository, fileStorageProvider);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            val viewModel = this.controller.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (SearchDocumentsUseCase.AuthenticationTokenNotFoundException | SearchDocumentsUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (SearchCriterionIndexInvalidException | PageIndexInvalidException | SearchDocumentsUseCase.PaginationInvalidException exception) {
            this.onException(exception);
        }
    }

    private SearchDocumentsController.@NonNull RequestObject collectRequestObject() throws SearchCriterionIndexInvalidException, PageIndexInvalidException {
        val searchCriterion = this.collectSearchCriterion();
        val searchText = this.ioProvider.readLine(Message.Format.PROMPT, "search text");
        val pageIndex = this.collectPageIndex();

        return SearchDocumentsController.RequestObject.of(searchCriterion, searchText, pageIndex, Message.Page.MAX_PAGE_SIZE);
    }

    private SearchDocumentsController.@NonNull SearchCriterion collectSearchCriterion() throws SearchCriterionIndexInvalidException {
        val rawSearchCriterionIndex = this.ioProvider.readLine(Message.Format.PROMPT, SEARCH_CRITERION_PROMPT);

        try {
            val searchCriterionIndex = Integer.parseUnsignedInt(rawSearchCriterionIndex);
            val searchCriterion = SearchDocumentsController.SearchCriterion.values()[searchCriterionIndex];

            return searchCriterion;

        } catch (Exception exception) {
            throw new SearchCriterionIndexInvalidException();
        }
    }

    private @Unsigned int collectPageIndex() throws PageIndexInvalidException {
        val rawPageIndex = this.ioProvider.readLine(Message.Format.PROMPT, "page number");

        try {
            val pageIndex = Integer.parseUnsignedInt(rawPageIndex);
            return pageIndex;

        } catch (NumberFormatException exception) {
            throw new PageIndexInvalidException();
        }
    }

    private void displayViewModel(SearchDocumentsController.@NonNull ViewModel viewModel) {
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

    private void onSuccess(SearchDocumentsController.@NonNull ViewModel viewModel) {
        this.nextViewClass = cachedViewClass;
        this.displayViewModel(viewModel);
    }

    private static class SearchCriterionIndexInvalidException extends Exception {}
    private static class PageIndexInvalidException extends Exception {}
}
