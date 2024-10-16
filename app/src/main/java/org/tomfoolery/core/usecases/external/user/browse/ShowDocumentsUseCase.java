package org.tomfoolery.core.usecases.external.user.browse;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.domain.auth.AuthenticationToken;
import org.tomfoolery.core.utils.functional.ThrowableFunction;

import java.util.Collection;

@RequiredArgsConstructor(staticName = "of")
public class ShowDocumentsUseCase implements ThrowableFunction<ShowDocumentsUseCase.Request, ShowDocumentsUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull AuthenticationTokenService authenticationTokenService;

    @Override
    public @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenInvalidException {
        val authenticationToken = request.getAuthenticationToken();

        ensureAuthenticationTokenIsValid(authenticationToken);

        val documents = this.documentRepository.show();
        return Response.of(documents);
    }

    private void ensureAuthenticationTokenIsValid(@NonNull AuthenticationToken authenticationToken) throws AuthenticationTokenInvalidException {
        if (!this.authenticationTokenService.verifyToken(authenticationToken))
            throw new AuthenticationTokenInvalidException();
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull AuthenticationToken authenticationToken;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Collection<Document> documents;
    }

    public static class AuthenticationTokenInvalidException extends Exception {}
}
