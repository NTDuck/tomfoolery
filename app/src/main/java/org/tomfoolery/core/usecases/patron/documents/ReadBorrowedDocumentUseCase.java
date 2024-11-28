package org.tomfoolery.core.usecases.patron.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.dataclasses.auth.security.AuthenticationToken;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

import java.util.Set;

public final class ReadBorrowedDocumentUseCase extends AuthenticatedUserUseCase implements ThrowableFunction<ReadBorrowedDocumentUseCase.Request, ReadBorrowedDocumentUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull PatronRepository patronRepository;

    public static @NonNull ReadBorrowedDocumentUseCase of(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ReadBorrowedDocumentUseCase(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ReadBorrowedDocumentUseCase(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
        this.patronRepository = patronRepository;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Patron.class);
    }

    @Override
    public @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PatronNotFoundException, DocumentNotFoundException, DocumentNotBorrowedException {
        val patronAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patron = this.getPatronFromAuthenticationToken(patronAuthenticationToken);

        val documentId = request.getDocumentId();
        val document = this.getDocumentFromId(documentId);

        this.ensureDocumentIsAlreadyBorrowed(patron, documentId);

        val documentContent = document.getContent();
        return Response.of(documentContent);
    }

    private @NonNull Patron getPatronFromAuthenticationToken(@NonNull AuthenticationToken patronAuthenticationToken) throws AuthenticationTokenInvalidException, PatronNotFoundException {
        val patronId = this.authenticationTokenGenerator.getUserIdFromAuthenticationToken(patronAuthenticationToken);

        if (patronId == null)
            throw new AuthenticationTokenInvalidException();

        val patron = this.patronRepository.getById(patronId);

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

    private void ensureDocumentIsAlreadyBorrowed(@NonNull Patron patron, Document.@NonNull Id documentId) throws DocumentNotBorrowedException {
        val borrowedDocumentIds = patron.getAudit().getBorrowedDocumentIds();

        if (!borrowedDocumentIds.contains(documentId))
            throw new DocumentNotBorrowedException();
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Document.@NonNull Id documentId;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        Document.@NonNull Content documentContent;
    }

    public static class PatronNotFoundException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
    public static class DocumentNotBorrowedException extends Exception {}
}
