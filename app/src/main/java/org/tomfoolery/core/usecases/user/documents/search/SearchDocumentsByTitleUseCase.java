package org.tomfoolery.core.usecases.user.documents.search;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.core.usecases.user.abc.SearchDocumentsByCriterionUseCase;
import org.tomfoolery.core.utils.contracts.functional.TriFunction;
import org.tomfoolery.core.utils.dataclasses.Page;

public final class SearchDocumentsByTitleUseCase extends SearchDocumentsByCriterionUseCase {
    public static @NonNull SearchDocumentsByTitleUseCase of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        return new SearchDocumentsByTitleUseCase(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    private SearchDocumentsByTitleUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    @Override
    protected @NonNull TriFunction<@NonNull String, @NonNull Integer, @NonNull Integer, @Nullable Page<FragmentaryDocument>> getPaginatedFragmentaryDocumentsFunction() {
        return this.documentRepository::searchPaginatedFragmentaryDocumentsByTitle;
    }
}
