package org.tomfoolery.core.usecases.user.documents.search;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.user.abc.SearchDocumentsByCriterionUseCase;
import org.tomfoolery.core.utils.contracts.functional.TriFunction;
import org.tomfoolery.core.utils.dataclasses.Page;

public final class SearchDocumentsByGenreUseCase extends SearchDocumentsByCriterionUseCase {
    public static @NonNull SearchDocumentsByGenreUseCase of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        return new SearchDocumentsByGenreUseCase(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    private SearchDocumentsByGenreUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    @Override
    protected @NonNull TriFunction<@NonNull String, @NonNull Integer, @NonNull Integer, @Nullable Page<Document>> getPaginatedDocumentsFunction() {
        return this.documentRepository::searchPaginatedDocumentsByGenre;
    }
}