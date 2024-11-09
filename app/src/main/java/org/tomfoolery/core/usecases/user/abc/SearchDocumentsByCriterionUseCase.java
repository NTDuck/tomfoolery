package org.tomfoolery.core.usecases.user.abc;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.core.utils.contracts.functional.TriFunction;
import org.tomfoolery.core.utils.dataclasses.Page;

public abstract class SearchDocumentsByCriterionUseCase extends AuthenticatedUserUseCase implements ThrowableFunction<SearchDocumentsByCriterionUseCase.Request, SearchDocumentsByCriterionUseCase.Response> {
    protected final @NonNull DocumentRepository documentRepository;

    protected SearchDocumentsByCriterionUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);
        this.documentRepository = documentRepository;
    }

    protected abstract @NonNull TriFunction<@NonNull String, @NonNull @Unsigned Integer, @NonNull @Unsigned Integer, @Nullable Page<FragmentaryDocument>> getPaginatedFragmentaryDocumentsFunction();

    @Override
    public final @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PaginationInvalidException {
        val userAuthenticationToken = getAuthenticationTokenFromRepository();
        ensureAuthenticationTokenIsValid(userAuthenticationToken);

        val searchText = request.getSearchText();
        val pageIndex = request.getPageIndex();
        val maxPageSize = request.getMaxPageSize();

        val paginatedFragmentaryDocuments = getPaginatedFragmentaryDocumentsByCriterion(searchText, pageIndex, maxPageSize);

        return Response.of(paginatedFragmentaryDocuments);
    }

    private @NonNull Page<FragmentaryDocument> getPaginatedFragmentaryDocumentsByCriterion(@NonNull String searchText, @Unsigned int pageIndex, @Unsigned int maxPageSize) throws PaginationInvalidException {
        val paginatedFragmentaryDocumentsFunction = getPaginatedFragmentaryDocumentsFunction();
        val paginatedFragmentaryDocuments = paginatedFragmentaryDocumentsFunction.apply(searchText, pageIndex, maxPageSize);

        if (paginatedFragmentaryDocuments == null)
            throw new PaginationInvalidException();

        return paginatedFragmentaryDocuments;
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String searchText;
        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Page<FragmentaryDocument> paginatedFragmentaryDocuments;
    }

    public static class PaginationInvalidException extends Exception {}
}
