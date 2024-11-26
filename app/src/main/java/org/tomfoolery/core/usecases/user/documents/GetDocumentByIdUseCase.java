package org.tomfoolery.core.usecases.user.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

public final class GetDocumentByIdUseCase extends AuthenticatedUserUseCase implements ThrowableFunction<GetDocumentByIdUseCase.Request, GetDocumentByIdUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    public static @NonNull GetDocumentByIdUseCase of(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetDocumentByIdUseCase(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetDocumentByIdUseCase(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
    }

    @Override
    public @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentNotFoundException {
        val authenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(authenticationToken);

        val documentId = request.getDocumentId();
        val fragmentaryDocument = this.getFragmentaryDocumentFromId(documentId);

        return Response.of(fragmentaryDocument);
    }

    private @NonNull FragmentaryDocument getFragmentaryDocumentFromId(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        val fragmentaryDocument = this.documentRepository.getFragmentById(documentId);

        if (fragmentaryDocument == null)
            throw new DocumentNotFoundException();

        return fragmentaryDocument;
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Document.@NonNull Id documentId;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull FragmentaryDocument fragmentaryDocument;
    }

    public static class DocumentNotFoundException extends Exception {}
}
