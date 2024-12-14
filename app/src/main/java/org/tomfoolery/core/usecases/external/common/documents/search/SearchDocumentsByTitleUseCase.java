package org.tomfoolery.core.usecases.external.common.documents.search;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.common.documents.search.abc.SearchDocumentsUseCase;

public final class SearchDocumentsByTitleUseCase extends SearchDocumentsUseCase {
    public static @NonNull SearchDocumentsByTitleUseCase of(@NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new SearchDocumentsByTitleUseCase(documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private SearchDocumentsByTitleUseCase(@NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    protected @NonNull DocumentSearchFunction getDocumentSearchFunction() {
        return this.documentSearchGenerator::searchPageByTitle;
    }
}
