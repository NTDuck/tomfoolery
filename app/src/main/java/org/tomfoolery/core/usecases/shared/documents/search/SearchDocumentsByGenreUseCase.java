package org.tomfoolery.core.usecases.shared.documents.search;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.shared.documents.search.abc.SearchDocumentsUseCase;

public final class SearchDocumentsByGenreUseCase extends SearchDocumentsUseCase {
    public static @NonNull SearchDocumentsByGenreUseCase of(@NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new SearchDocumentsByGenreUseCase(documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private SearchDocumentsByGenreUseCase(@NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    protected @NonNull DocumentSearchFunction getDocumentSearchFunction() {
        return this.documentSearchGenerator::searchPaginatedByGenre;
    }
}

