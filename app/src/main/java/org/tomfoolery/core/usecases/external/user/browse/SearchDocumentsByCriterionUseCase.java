package org.tomfoolery.core.usecases.external.user.browse;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.Page;
import org.tomfoolery.core.utils.dataclasses.SearchCriterion;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.core.utils.enums.DocumentAttributeName;

@RequiredArgsConstructor(staticName = "of")
public class SearchDocumentsByCriterionUseCase implements ThrowableFunction<SearchDocumentsByCriterionUseCase.Request, SearchDocumentsByCriterionUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    private final @NonNull AuthenticationTokenService authenticationTokenService;
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository;

    @Override
    public final @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenInvalidException, AuthenticationTokenNotFoundException {
        val criterion = request.getSearchCriterion();
        val pageIndex = request.getPageIndex();
        val pageSize = request.getPageSize();

        val authenticationToken = getAuthenticationTokenFromRepository();
        ensureAuthenticationTokenIsValid(authenticationToken);

        val paginatedDocuments = getPaginatedDocuments(criterion, pageIndex, pageSize);
        return Response.of(paginatedDocuments);
    }

    private @NonNull AuthenticationToken getAuthenticationTokenFromRepository() throws AuthenticationTokenNotFoundException {
        val authenticationToken = this.authenticationTokenRepository.getToken();

        if (authenticationToken == null)
            throw new AuthenticationTokenNotFoundException();

        return authenticationToken;
    }

    private @NonNull Page<Document> getPaginatedDocuments(@NonNull SearchCriterion<DocumentAttributeName, String> searchCriterion, int pageIndex, int pageSize) {
        return this.documentRepository.searchPaginatedByCriterion(searchCriterion, pageIndex, pageSize);
    }

    private void ensureAuthenticationTokenIsValid(@NonNull AuthenticationToken authenticationToken) throws AuthenticationTokenInvalidException {
        if (!this.authenticationTokenService.verifyToken(authenticationToken))
            throw new AuthenticationTokenInvalidException();
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull SearchCriterion<DocumentAttributeName, String> searchCriterion;
        int pageIndex;
        int pageSize;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Page<Document> paginatedDocuments;
    }

    public static class AuthenticationTokenNotFoundException extends Exception {}
    public static class AuthenticationTokenInvalidException extends Exception {}
}
