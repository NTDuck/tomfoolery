package org.tomfoolery.core.usecases.user.documents.search.abc;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.core.utils.dataclasses.common.Page;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public abstract class SearchDocumentsUseCase extends AuthenticatedUserUseCase implements ThrowableFunction<SearchDocumentsUseCase.Request, SearchDocumentsUseCase.Response> {
    private static final @NonNull Duration SYNCHRONIZATION_INTERVAL = Duration.ofSeconds(1);

    private final @NonNull DocumentRepository documentRepository;
    protected final @NonNull DocumentSearchGenerator documentSearchGenerator;

    protected SearchDocumentsUseCase(@NonNull DocumentRepository documentRepository, @NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
        this.documentSearchGenerator = documentSearchGenerator;
    }

    protected abstract @NonNull DocumentSearchFunction getDocumentSearchFunction();

    @Override
    public final @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PaginationInvalidException {
        val userAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(userAuthenticationToken);

        this.synchronizeGeneratorIfIntervalIsElapsed();

        val searchTerm = request.getSearchTerm().toLowerCase();
        val pageIndex = request.getPageIndex();
        val maxPageSize = request.getMaxPageSize();

        val paginatedFragmentaryDocuments = this.searchDocuments(searchTerm, pageIndex, maxPageSize);

        return Response.of(paginatedFragmentaryDocuments);
    }

    private void synchronizeGeneratorIfIntervalIsElapsed() {
        if (this.documentSearchGenerator.isSynchronizedIntervalElapsed(SYNCHRONIZATION_INTERVAL))
            CompletableFuture.runAsync(() -> this.documentSearchGenerator.synchronizeWithRepository(this.documentRepository));
    }

    private @NonNull Page<FragmentaryDocument> searchDocuments(@NonNull String searchTerm, @Unsigned int pageIndex, @Unsigned int maxPageSize) throws PaginationInvalidException {
        val documentSearchFunction = this.getDocumentSearchFunction();
        val paginatedFragmentaryDocuments = documentSearchFunction.apply(searchTerm, pageIndex, maxPageSize);

        if (paginatedFragmentaryDocuments == null)
            throw new PaginationInvalidException();

        return paginatedFragmentaryDocuments;
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String searchTerm;
        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Page<FragmentaryDocument> paginatedFragmentaryDocuments;
    }

    public static class PaginationInvalidException extends Exception {}

    @FunctionalInterface
    protected interface DocumentSearchFunction {
        @Nullable Page<FragmentaryDocument> apply(@NonNull String searchTerm, @Unsigned int pageIndex, @Unsigned int maxPageSize);
    }
}
