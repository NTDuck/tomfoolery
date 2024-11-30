package org.tomfoolery.configurations.monolith.console.views.action.user.documents.search;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.utils.helpers.EnumResolver;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.documents.retrieval.search.abc.SearchDocumentsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.user.documents.SearchDocumentsController;

import java.util.stream.Collectors;

public final class SearchDocumentsActionView extends UserActionView {
    private static final @Unsigned int MAX_PAGE_SIZE = 7;

    private static final @NonNull String SEARCH_CRITERION_PROMPT = String.format("search criterion (%s)", EnumResolver.getEnumeratedNames(
        SearchDocumentsController.SearchCriterion.class, "%s %d", 0
    ).stream().collect(Collectors.joining(", ")));

    private static final @NonNull String SEARCH_PATTERN_PROMPT = String.format("search pattern (%s)", EnumResolver.getEnumeratedNames(
        SearchDocumentsController.SearchPattern.class, "%s %d", 0
    ).stream().collect(Collectors.joining(", ")));

    private final @NonNull SearchDocumentsController controller;

    public static @NonNull SearchDocumentsActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new SearchDocumentsActionView(ioProvider, documentRepository, documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private SearchDocumentsActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.controller = SearchDocumentsController.of(documentRepository, documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            val viewModel = this.controller.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (SearchCriterionIndexInvalidException exception) {
            this.onSearchCriterionIndexInvalidException();
        } catch (SearchPatternIndexInvalidException exception) {
            this.onSearchPatternIndexInvalidException();
        } catch (PageIndexInvalidException exception) {
            this.onPageIndexInvalidException();

        } catch (SearchDocumentsUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (SearchDocumentsUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (SearchDocumentsUseCase.PaginationInvalidException exception) {
            this.onPaginationInvalidException();
        }
    }

    private SearchDocumentsController.@NonNull RequestObject collectRequestObject() throws SearchCriterionIndexInvalidException, SearchPatternIndexInvalidException, PageIndexInvalidException {
        val searchCriterion = this.collectSearchCriterion();
        val searchPattern = this.collectSearchPattern();

        val searchText = this.ioProvider.readLine(Message.Format.PROMPT, "search text");
        val pageIndex = this.collectPageIndex();

        return SearchDocumentsController.RequestObject.of(searchCriterion, searchPattern, searchText, pageIndex, MAX_PAGE_SIZE);
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

    private SearchDocumentsController.@NonNull SearchPattern collectSearchPattern() throws SearchPatternIndexInvalidException {
        val rawSearchPatternIndex = this.ioProvider.readLine(Message.Format.PROMPT, SEARCH_PATTERN_PROMPT);

        try {
            val searchPatternIndex = Integer.parseUnsignedInt(rawSearchPatternIndex);
            val searchPattern = SearchDocumentsController.SearchPattern.values()[searchPatternIndex];

            return searchPattern;

        } catch (Exception exception) {
            throw new SearchPatternIndexInvalidException();
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

    private void onSuccess(SearchDocumentsController.@NonNull ViewModel viewModel) {
        this.nextViewClass = cachedViewClass;
        this.displayViewModel(viewModel);
    }

    private void displayViewModel(SearchDocumentsController.@NonNull ViewModel viewModel) {
        val pageIndex = viewModel.getPageIndex();
        val maxPageIndex = viewModel.getMaxPageIndex();

        this.ioProvider.writeLine("Showing matching documents, page %d of %d", pageIndex, maxPageIndex);

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

    private void onSearchCriterionIndexInvalidException() {
        this.nextViewClass = cachedViewClass;

        this.ioProvider.writeLine(Message.Format.ERROR, "Search criterion must be a valid number");
    }

    private void onSearchPatternIndexInvalidException() {
        this.nextViewClass = cachedViewClass;

        this.ioProvider.writeLine(Message.Format.ERROR, "Search pattern must be a valid number");
    }

    private void onPaginationInvalidException() {
        this.nextViewClass = cachedViewClass;

        this.ioProvider.writeLine(Message.Format.ERROR, "Found no documents with such page number");
    }

    private static class SearchCriterionIndexInvalidException extends Exception {}
    private static class SearchPatternIndexInvalidException extends Exception {}
    private static class PageIndexInvalidException extends Exception {}
}
