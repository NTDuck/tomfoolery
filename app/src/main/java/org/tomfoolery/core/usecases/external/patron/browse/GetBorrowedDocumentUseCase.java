package org.tomfoolery.core.usecases.external.patron.browse;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.dataproviders.PatronRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.domain.Patron;
import org.tomfoolery.core.domain.auth.AuthenticationToken;
import org.tomfoolery.core.utils.function.ThrowableFunction;

@RequiredArgsConstructor(staticName = "of")
public class GetBorrowedDocumentUseCase implements ThrowableFunction<GetBorrowedDocumentUseCase.Request, GetBorrowedDocumentUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull PatronRepository patronRepository;

    private final @NonNull AuthenticationTokenService authenticationTokenService;

    @Override
    public @NonNull Response apply(@NonNull Request request) throws PatronAuthenticationTokenInvalidException, PatronNotFoundException, DocumentNotFoundException, DocumentNotBorrowedException {
        val patronAuthenticationToken = request.getPatronAuthenticationToken();
        val documentId = request.getDocumentId();

        ensurePatronAuthenticationTokenIsValid(patronAuthenticationToken);

        val patron = getPatronFromAuthenticationToken(patronAuthenticationToken);
        val document = getDocumentFromId(documentId);

        ensureDocumentIsBorrowed(patron, documentId);

        return Response.of(document);
    }

    private void ensurePatronAuthenticationTokenIsValid(@NonNull AuthenticationToken patronAuthenticationToken) throws PatronAuthenticationTokenInvalidException {
        if (!this.authenticationTokenService.verifyToken(patronAuthenticationToken, Patron.class))
            throw new PatronAuthenticationTokenInvalidException();
    }

    private @NonNull Patron getPatronFromAuthenticationToken(@NonNull AuthenticationToken staffAuthenticationToken) throws PatronNotFoundException {
        val patronId = this.authenticationTokenService.getUserIdFromToken(staffAuthenticationToken);
        val patron = patronRepository.getById(patronId);

        if (patron == null)
            throw new PatronNotFoundException();

        return patron;
    }

    private @NonNull Document getDocumentFromId(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        val document = this.documentRepository.getById(documentId);

        if (document == null)
            throw new DocumentNotFoundException();

        return document;
    }

    private static void ensureDocumentIsBorrowed(@NonNull Patron patron, Document.@NonNull Id documentId) throws DocumentNotBorrowedException {
        val patronAudit = patron.getAudit();
        val borrowedDocumentIdsOfPatron = patronAudit.getBorrowedDocumentIds();

        if (!borrowedDocumentIdsOfPatron.contains(documentId))
            throw new DocumentNotBorrowedException();
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull AuthenticationToken patronAuthenticationToken;
        Document.@NonNull Id documentId;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Document document;
    }

    public static class PatronAuthenticationTokenInvalidException extends Exception {}
    public static class PatronNotFoundException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
    public static class DocumentNotBorrowedException extends Exception {}
}
