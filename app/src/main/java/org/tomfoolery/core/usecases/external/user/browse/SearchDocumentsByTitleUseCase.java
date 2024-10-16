package org.tomfoolery.core.usecases.external.user.browse;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.domain.Document;

import java.util.Collection;

public class SearchDocumentsByTitleUseCase extends SearchDocumentsByCriterionUseCase {
    private final @NonNull DocumentRepository documentRepository;

    private SearchDocumentsByTitleUseCase(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenService authenticationTokenService) {
        super(authenticationTokenService);
        this.documentRepository = documentRepository;
    }

    public @NonNull SearchDocumentsByTitleUseCase of(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenService authenticationTokenService) {
        return new SearchDocumentsByTitleUseCase(documentRepository, authenticationTokenService);
    }

    @Override
    protected @NonNull Collection<Document> getDocumentsFromCriterion(@NonNull String criterion) {
        return this.documentRepository.searchByTitle(criterion);
    }
}
