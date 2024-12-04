package org.tomfoolery.core.usecases.common.documents.references;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
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
    public @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentISBNInvalidException, DocumentNotFoundException {
        val authenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(authenticationToken);

        val documentISBN = request.getDocumentISBN();
        val documentId = this.getDocumentIdFromISBN(documentISBN);
        val document = this.getDocumentById(documentId);

        val documentUrl = this.documentUrlGenerator.generateUrlFromDocument(document);
        val documentQrCode = this.documentQrCodeGenerator.generateQrCodeFromUrl(documentUrl);

        return Response.of(documentQrCode);
    }

    private Document.@NonNull Id getDocumentIdFromISBN(@NonNull String documentISBN) throws DocumentISBNInvalidException {
        val documentId = Document.Id.of(documentISBN);

        if (documentId == null)
            throw new DocumentISBNInvalidException();

        return documentId;
    }

    private @NonNull Document getDocumentById(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        val document = this.documentRepository.getById(documentId);

        if (document == null)
            throw new DocumentNotFoundException();

        return document;
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String documentISBN;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        Document.@NonNull QrCode documentQrCode;
    }

    public static class DocumentISBNInvalidException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
}
