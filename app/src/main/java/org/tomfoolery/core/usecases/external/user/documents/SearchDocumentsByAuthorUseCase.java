package org.tomfoolery.core.usecases.external.user.documents;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.external.user.abc.SearchDocumentsByCriterionUseCase;
import org.tomfoolery.core.utils.dataclasses.Page;

public final class SearchDocumentsByAuthorUseCase extends SearchDocumentsByCriterionUseCase {
    public static @NonNull SearchDocumentsByAuthorUseCase of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        return new SearchDocumentsByAuthorUseCase(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    private SearchDocumentsByAuthorUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    @Override
    protected @Nullable Page<Document> getPaginatedDocumentsFromCriterion(@NonNull String criterion, int pageIndex, int pageSize) {
        return this.documentRepository.searchPaginatedDocumentsByAuthor(criterion, pageIndex, pageSize);
    }
}