package org.tomfoolery.core.usecases.user.documents.search;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.core.usecases.user.abc.SearchDocumentsUseCase;
import org.tomfoolery.core.utils.contracts.functional.TriFunction;
import org.tomfoolery.core.utils.dataclasses.common.Page;

public final class SearchDocumentsByGenreUseCase extends SearchDocumentsUseCase {
    public static @NonNull SearchDocumentsByGenreUseCase of(@NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new SearchDocumentsByGenreUseCase(documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private SearchDocumentsByGenreUseCase(@NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(documentSearchGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    protected @NonNull TriFunction<@NonNull String, @NonNull Integer, @NonNull Integer, @Nullable Page<FragmentaryDocument>> getDocumentSearchFunction() {
        return this.documentSearchGenerator::searchPaginatedDocumentsByGenre;
    }
}