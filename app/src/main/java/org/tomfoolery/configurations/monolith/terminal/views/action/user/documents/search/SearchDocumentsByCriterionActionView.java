package org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.search;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.action.abc.ActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.abc.SharedUserActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.ShowDocumentsActionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.user.documents.search.abc.SearchDocumentsUseCase;
import org.tomfoolery.core.usecases.user.documents.search.SearchDocumentsByAuthorSubsequenceUseCase;
import org.tomfoolery.core.usecases.user.documents.search.SearchDocumentsByGenreSubsequenceUseCase;
import org.tomfoolery.core.usecases.user.documents.search.SearchDocumentsByTitleSubsequenceUseCase;
import org.tomfoolery.infrastructures.adapters.presenters.user.documents.search.SearchDocumentsByCriterionPresenter;

import java.util.Map;

public final class SearchDocumentsByCriterionActionView extends SharedUserActionView {
    private final @NonNull Map<Integer, SearchDocumentsUseCase> useCases;
    private final @NonNull SearchDocumentsByCriterionPresenter presenter;

    private int criterionIndex;

    public static @NonNull SearchDocumentsByCriterionActionView of(@NonNull IOHandler ioHandler, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        return new SearchDocumentsByCriterionActionView(ioHandler, authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    private SearchDocumentsByCriterionActionView(@NonNull IOHandler ioHandler, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        super(ioHandler, authenticationTokenGenerator, authenticationTokenRepository);

        this.useCases = Map.of(
            0, SearchDocumentsByTitleSubsequenceUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository),
            1, SearchDocumentsByAuthorSubsequenceUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository),
            2, SearchDocumentsByGenreSubsequenceUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository)
        );
        this.presenter = SearchDocumentsByCriterionPresenter.of();
    }

    @Override
    public void run() {
        try {
            val requestModel = collectRequestModel();
            val useCase = this.useCases.get(this.criterionIndex);
            val responseModel = useCase.apply(requestModel);
        }
    }

    private SearchDocumentsUseCase.@NonNull Request collectRequestModel() {
        val criterionIndexAsString = this.ioHandler.readLine(PROMPT_MESSAGE_FORMAT, "search criterion (0 for author, 1 for title, 2 for genre");
        this.criterionIndex = Integer.parseUnsignedInt(criterionIndexAsString);

        val criterion = this.ioHandler.readLine(PROMPT_MESSAGE_FORMAT, "search criterion value");

        try {
            val pageIndex = Integer.parseUnsignedInt(pageIndexAsString);
        } catch (NumberFormatException exception) {
            throw new ShowDocumentsActionView.PageIndexInvalidException();
        }

        return SearchDocumentsUseCase.Request.of(criterion, pageIndex, MAX_PAGE_SIZE);
    }

    private static class CriterionIndexInvalidException extends Exception {}
    private static class PageIndexInvalidException extends Exception {}
}
