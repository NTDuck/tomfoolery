package org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.search;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.EnumResolver;
import org.tomfoolery.configurations.monolith.terminal.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.ShowDocumentsActionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.user.documents.search.abc.SearchDocumentsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.user.documents.SearchDocumentsController;

import java.util.List;
import java.util.stream.Collectors;


public final class SearchDocumentsActionView extends UserActionView {
    private final @NonNull SearchDocumentsController controller;

    public static @NonNull SearchDocumentsActionView of(@NonNull IOHandler ioHandler, @NonNull DocumentRepository documentRepository, @NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new SearchDocumentsActionView(ioHandler, documentRepository, documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private SearchDocumentsActionView(@NonNull IOHandler ioHandler, @NonNull DocumentRepository documentRepository, @NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioHandler);

        this.controller = SearchDocumentsController.of(documentRepository, documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            val requestObject = collectRequestObject();
            val useCase = this.useCases.get(this.criterionIndex);
            val responseModel = useCase.apply(requestObject);
        }
    }

    private SearchDocumentsController.@NonNull RequestObject collectRequestObject() {
        int index = 0;

        val rawSearchCriterionIndex = this.ioHandler.readLine(Message.Format.PROMPT, )
    }

    private static @NonNull String getSearchCriterionSelectionPrompt() {
        val names = EnumResolver.getNames(SearchDocumentsController.SearchCriterion.class);

        int index = 0;
        val stringBuilder = new StringBuilder();

        for ()
    }

    private static class CriterionIndexInvalidException extends Exception {}
    private static class PageIndexInvalidException extends Exception {}
}
