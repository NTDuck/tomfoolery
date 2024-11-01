package org.tomfoolery.core.usecases.external.user.documents.references;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.documents.references.DocumentQrCodeGenerator;
import org.tomfoolery.core.dataproviders.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.external.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

public final class GetDocumentQrCodeUseCase extends AuthenticatedUserUseCase implements ThrowableFunction<GetDocumentQrCodeUseCase.Request, GetDocumentQrCodeUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull DocumentQrCodeGenerator documentQrCodeGenerator;
    private final @NonNull DocumentUrlGenerator documentUrlGenerator;

    public static @NonNull GetDocumentQrCodeUseCase of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull DocumentQrCodeGenerator documentQrCodeGenerator, @NonNull DocumentUrlGenerator documentUrlGenerator) {
        return new GetDocumentQrCodeUseCase(authenticationTokenGenerator, authenticationTokenRepository, documentRepository, documentQrCodeGenerator, documentUrlGenerator);
    }

    private GetDocumentQrCodeUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull DocumentQrCodeGenerator documentQrCodeGenerator, @NonNull DocumentUrlGenerator documentUrlGenerator) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
        this.documentQrCodeGenerator = documentQrCodeGenerator;
        this.documentUrlGenerator = documentUrlGenerator;
    }

    @Override
    public @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentNotFoundException {
        val authenticationToken = getAuthenticationTokenFromRepository();
        ensureAuthenticationTokenIsValid(authenticationToken);

        val documentId = request.getDocumentId();
        val document = getDocumentFromId(documentId);

        val documentUrl = this.documentUrlGenerator.generateUrl(document);
        val documentQrCode = this.documentQrCodeGenerator.generateQrCodeFromUrl(documentUrl);

        return Response.of(documentQrCode);
    }

    private @NonNull Document getDocumentFromId(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        val document = this.documentRepository.getById(documentId);

        if (document == null)
            throw new DocumentNotFoundException();

        return document;
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Document.@NonNull Id documentId;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        Document.@NonNull QrCode documentQrCode;
    }

    public static class DocumentNotFoundException extends Exception {}
}
