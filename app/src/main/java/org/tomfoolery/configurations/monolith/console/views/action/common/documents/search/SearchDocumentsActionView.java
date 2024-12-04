package org.tomfoolery.configurations.monolith.console.views.action.common.documents.search;

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
import org.tomfoolery.core.usecases.common.documents.search.abc.SearchDocumentsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.common.documents.search.SearchDocumentsController;

import java.util.stream.Collectors;

public final class SearchDocumentsActionView extends UserActionView {
    private static final @NonNull String SEARCH_CRITERION_PROMPT = String.format("search criterion (%s)", EnumResolver.getEnumeratedNames(
        SearchDocumentsController.SearchCriterion.class, "%s (%d)", 0
    ).stream().collect(Collectors.joining(", ")));

    private final @NonNull SearchDocumentsController controller;

    public static @NonNull SearchDocumentsActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new SearchDocumentsActionView(ioProvider, documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private SearchDocumentsActionView(@NonNull IOProvider ioProvider, @NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.controller = SearchDocumentsController.of(documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
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

        viewModel.getPaginatedDocuments()
            .forEach(document -> {
                this.ioProvider.writeLine("- [%s] %s", document.getDocumentISBN_10(), document.getDocumentTitle());
            });
    }

    private void onSuccess(SearchDocumentsController.@NonNull ViewModel viewModel) {
        this.nextViewClass = cachedViewClass;
        this.displayViewModel(viewModel);
    }

    private static class SearchCriterionIndexInvalidException extends Exception {}
    private static class PageIndexInvalidException extends Exception {}
}
