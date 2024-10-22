package org.tomfoolery.core.usecases.external.user.browse;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.Page;
import org.tomfoolery.core.utils.dataclasses.SearchCriterion;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.core.utils.enums.DocumentAttributeName;

@RequiredArgsConstructor(staticName = "of")
public abstract class SearchDocumentsByCriterionUseCase implements ThrowableFunction<SearchDocumentsByCriterionUseCase.Request, SearchDocumentsByCriterionUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull AuthenticationTokenService authenticationTokenService;

    @Override
    public final @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenInvalidException {
        val authenticationToken = request.getAuthenticationToken();
        val criterion = request.getSearchCriterion();
        val pageIndex = request.getPageIndex();
        val pageSize = request.getPageSize();

        ensureAuthenticationTokenIsValid(authenticationToken);

        val paginatedDocuments = getPaginatedDocuments(criterion, pageIndex, pageSize);
        return Response.of(paginatedDocuments);
    }

    private @NonNull Page<Document> getPaginatedDocuments(@NonNull SearchCriterion<DocumentAttributeName, String> searchCriterion, int pageIndex, int pageSize) {
        return this.documentRepository.searchPaginatedEntitiesByCriterion(searchCriterion, pageIndex, pageSize);
    }

    private void ensureAuthenticationTokenIsValid(@NonNull AuthenticationToken authenticationToken) throws AuthenticationTokenInvalidException {
        if (!this.authenticationTokenService.verifyToken(authenticationToken))
            throw new AuthenticationTokenInvalidException();
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull AuthenticationToken authenticationToken;

        @NonNull SearchCriterion<DocumentAttributeName, String> searchCriterion;
        int pageIndex;
        int pageSize;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Page<Document> paginatedDocuments;
    }

    public static class AuthenticationTokenInvalidException extends Exception {}
}
