package org.tomfoolery.core.usecases.user.documents.search;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.user.documents.search.abc.SearchDocumentsUseCase;

public final class SearchDocumentsByGenreSuffixUseCase extends SearchDocumentsUseCase {
    public static @NonNull SearchDocumentsByGenreSuffixUseCase of(@NonNull DocumentRepository documentRepository, @NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new SearchDocumentsByGenreSuffixUseCase(documentRepository, documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private SearchDocumentsByGenreSuffixUseCase(@NonNull DocumentRepository documentRepository, @NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(documentRepository, documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    protected @NonNull DocumentSearchFunction getDocumentSearchFunction() {
        return this.documentSearchGenerator::searchPaginatedDocumentsByGenreSuffix;
    }
}