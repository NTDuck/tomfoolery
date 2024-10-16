package org.tomfoolery.core.usecases.external.user.browse;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.domain.Document;

import java.util.Collection;

public class SearchDocumentsByGenreUseCase extends SearchDocumentsByCriterionUseCase {
    private final @NonNull DocumentRepository documentRepository;

    private SearchDocumentsByGenreUseCase(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenService authenticationTokenService) {
        super(authenticationTokenService);
        this.documentRepository = documentRepository;
    }

    public @NonNull SearchDocumentsByGenreUseCase of(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenService authenticationTokenService) {
        return new SearchDocumentsByGenreUseCase(documentRepository, authenticationTokenService);
    }

    @Override
    protected @NonNull Collection<Document> getDocumentsFromCriterion(@NonNull String criterion) {
        return this.documentRepository.searchByGenre(criterion);
    }
}
