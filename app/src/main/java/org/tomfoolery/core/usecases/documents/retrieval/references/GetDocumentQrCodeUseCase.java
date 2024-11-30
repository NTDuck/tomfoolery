package org.tomfoolery.core.usecases.documents.retrieval.references;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentQrCodeGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

public final class GetDocumentQrCodeUseCase extends AuthenticatedUserUseCase implements ThrowableFunction<GetDocumentQrCodeUseCase.Request, GetDocumentQrCodeUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    private final @NonNull DocumentQrCodeGenerator documentQrCodeGenerator;
    private final @NonNull DocumentUrlGenerator documentUrlGenerator;;

    public static @NonNull GetDocumentQrCodeUseCase of(@NonNull DocumentRepository documentRepository, @NonNull DocumentQrCodeGenerator documentQrCodeGenerator, @NonNull DocumentUrlGenerator documentUrlGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetDocumentQrCodeUseCase(documentRepository, documentQrCodeGenerator, documentUrlGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetDocumentQrCodeUseCase(@NonNull DocumentRepository documentRepository, @NonNull DocumentQrCodeGenerator documentQrCodeGenerator, @NonNull DocumentUrlGenerator documentUrlGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;

        this.documentQrCodeGenerator = documentQrCodeGenerator;
        this.documentUrlGenerator = documentUrlGenerator;
    }

    @Override
    public @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentNotFoundException {
        val authenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(authenticationToken);

        val documentId = request.getDocumentId();
        val fragmentaryDocument = this.getDocumentWithoutContentById(documentId);

        val documentUrl = this.documentUrlGenerator.generateUrlFromDocument(fragmentaryDocument);
        val documentQrCode = this.documentQrCodeGenerator.generateQrCodeFromUrl(documentUrl);

        return Response.of(documentQrCode);
    }

    private @NonNull DocumentWithoutContent getDocumentWithoutContentById(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        val documentWithoutContent = this.documentRepository.getByIdWithoutContent(documentId);

        if (documentWithoutContent == null)
            throw new DocumentNotFoundException();

        return documentWithoutContent;
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
