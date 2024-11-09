package org.tomfoolery.core.usecases.user.abc;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
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

    protected abstract @NonNull TriFunction<@NonNull String, @NonNull Integer, @NonNull Integer, @Nullable Page<FragmentaryDocument>> getPaginatedFragmentaryDocumentsFunction();

    @Override
    public final @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PaginationInvalidException {
        val userAuthenticationToken = getAuthenticationTokenFromRepository();
        ensureAuthenticationTokenIsValid(userAuthenticationToken);

        val criterion = request.getCriterion();
        val pageIndex = request.getPageIndex();
        val pageSize = request.getPageSize();

        val paginatedFragmentaryDocuments = getPaginatedFragmentaryDocumentsByCriterion(criterion, pageIndex, pageSize);

        return Response.of(paginatedFragmentaryDocuments);
    }

    private @NonNull Page<FragmentaryDocument> getPaginatedFragmentaryDocumentsByCriterion(@NonNull String criterion, int pageIndex, int pageSize) throws PaginationInvalidException {
        val paginatedFragmentaryDocumentsFunction = getPaginatedFragmentaryDocumentsFunction();
        val paginatedFragmentaryDocuments = paginatedFragmentaryDocumentsFunction.apply(criterion, pageIndex, pageSize);

        if (paginatedFragmentaryDocuments == null)
            throw new PaginationInvalidException();

        return paginatedFragmentaryDocuments;
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String criterion;
        int pageIndex;
        int pageSize;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Page<FragmentaryDocument> paginatedFragmentaryDocuments;
    }

    public static class PaginationInvalidException extends Exception {}
}
