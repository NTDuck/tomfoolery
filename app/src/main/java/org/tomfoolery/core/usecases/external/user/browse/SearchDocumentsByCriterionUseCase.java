package org.tomfoolery.core.usecases.external.user.browse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.domain.auth.AuthenticationToken;
import org.tomfoolery.core.utils.functional.ThrowableFunction;

import java.util.Collection;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class SearchDocumentsByCriterionUseCase implements ThrowableFunction<SearchDocumentsByCriterionUseCase.Request, SearchDocumentsByCriterionUseCase.Response> {
    private final @NonNull AuthenticationTokenService authenticationTokenService;

    @Override
    public final @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenInvalidException {
        val authenticationToken = request.getAuthenticationToken();
        val criterion = request.getCriterion();

        ensureAuthenticationTokenIsValid(authenticationToken);

        val documents = getDocumentsFromCriterion(criterion);
        return Response.of(documents);
    }

    protected abstract @NonNull Collection<Document> getDocumentsFromCriterion(@NonNull String criterion);

    private void ensureAuthenticationTokenIsValid(@NonNull AuthenticationToken authenticationToken) throws AuthenticationTokenInvalidException {
        if (!this.authenticationTokenService.verifyToken(authenticationToken))
            throw new AuthenticationTokenInvalidException();
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull AuthenticationToken authenticationToken;
        @NonNull String criterion;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Collection<Document> documents;
    }

    public static class AuthenticationTokenInvalidException extends Exception {}
}
