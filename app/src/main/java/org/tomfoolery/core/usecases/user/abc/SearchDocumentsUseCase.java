package org.tomfoolery.core.usecases.user.abc;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.core.utils.contracts.functional.TriFunction;
import org.tomfoolery.core.utils.dataclasses.common.Page;

public abstract class SearchDocumentsUseCase extends AuthenticatedUserUseCase implements ThrowableFunction<SearchDocumentsUseCase.Request, SearchDocumentsUseCase.Response> {
    protected final @NonNull DocumentSearchGenerator documentSearchGenerator;

    protected SearchDocumentsUseCase(@NonNull DocumentSearchGenerator documentSearchGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentSearchGenerator = documentSearchGenerator;
    }

    protected abstract @NonNull TriFunction<@NonNull String, @NonNull @Unsigned Integer, @NonNull @Unsigned Integer, @Nullable Page<FragmentaryDocument>> getDocumentSearchFunction();

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
}