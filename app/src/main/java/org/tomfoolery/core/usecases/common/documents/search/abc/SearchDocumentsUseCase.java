package org.tomfoolery.core.usecases.common.documents.search.abc;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.core.utils.dataclasses.Page;

public abstract class SearchDocumentsUseCase extends AuthenticatedUserUseCase implements ThrowableFunction<SearchDocumentsUseCase.Request, SearchDocumentsUseCase.Response> {
    protected final @NonNull DocumentSearchGenerator documentSearchGenerator;

    protected SearchDocumentsUseCase(@NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentSearchGenerator = documentSearchGenerator;
    }

    protected abstract @NonNull DocumentSearchFunction getDocumentSearchFunction();

    @Override
    public final @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PaginationInvalidException {
        val userAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(userAuthenticationToken);

        val searchTerm = request.getSearchTerm();
        val pageIndex = request.getPageIndex();
        val maxPageSize = request.getMaxPageSize();

        val paginatedFragmentaryDocuments = this.searchDocuments(searchTerm, pageIndex, maxPageSize);

        return Response.of(paginatedFragmentaryDocuments);
    }

    private @NonNull Page<Document> searchDocuments(@NonNull String searchTerm, @Unsigned int pageIndex, @Unsigned int maxPageSize) throws PaginationInvalidException {
        val documentSearchFunction = this.getDocumentSearchFunction();
        val paginatedDocuments = documentSearchFunction.apply(searchTerm, pageIndex, maxPageSize);

        if (paginatedDocuments == null)
            throw new PaginationInvalidException();

        return paginatedDocuments;
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String searchTerm;

        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Page<Document> paginatedDocuments;
    }

    public static class PaginationInvalidException extends Exception {}

    @FunctionalInterface
    protected interface DocumentSearchFunction {
        @Nullable Page<Document> apply(@NonNull String searchTerm, @Unsigned int pageIndex, @Unsigned int maxPageSize);
    }
}
