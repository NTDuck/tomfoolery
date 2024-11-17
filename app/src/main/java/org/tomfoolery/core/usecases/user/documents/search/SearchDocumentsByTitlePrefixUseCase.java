package org.tomfoolery.core.usecases.user.documents.search;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.user.documents.search.abc.SearchDocumentsUseCase;

public final class SearchDocumentsByTitlePrefixUseCase extends SearchDocumentsUseCase {
    public static @NonNull SearchDocumentsByTitlePrefixUseCase of(@NonNull DocumentRepository documentRepository, @NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new SearchDocumentsByTitlePrefixUseCase(documentRepository, documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private SearchDocumentsByTitlePrefixUseCase(@NonNull DocumentRepository documentRepository, @NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(documentRepository, documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    protected @NonNull DocumentSearchFunction getDocumentSearchFunction() {
        return this.documentSearchGenerator::searchPaginatedDocumentsByTitlePrefix;
    }
}