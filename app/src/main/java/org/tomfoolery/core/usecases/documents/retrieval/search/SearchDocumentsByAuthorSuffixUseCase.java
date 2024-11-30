package org.tomfoolery.core.usecases.documents.retrieval.search;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.documents.retrieval.search.abc.SearchDocumentsUseCase;

public final class SearchDocumentsByAuthorSuffixUseCase extends SearchDocumentsUseCase {
    public static @NonNull SearchDocumentsByAuthorSuffixUseCase of(@NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new SearchDocumentsByAuthorSuffixUseCase(documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private SearchDocumentsByAuthorSuffixUseCase(@NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    protected @NonNull DocumentSearchFunction getDocumentSearchFunction() {
        return this.documentSearchGenerator::searchPaginatedDocumentsByAuthorPrefix;
    }
}