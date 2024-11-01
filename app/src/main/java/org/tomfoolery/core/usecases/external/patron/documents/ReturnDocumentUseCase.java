package org.tomfoolery.core.usecases.external.patron.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.usecases.external.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

import java.util.Collection;
import java.util.List;

public final class ReturnDocumentUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<ReturnDocumentUseCase.Request> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull PatronRepository patronRepository;

    public static @NonNull ReturnDocumentUseCase of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository) {
        return new ReturnDocumentUseCase(authenticationTokenGenerator, authenticationTokenRepository, documentRepository, patronRepository);
    }

    private ReturnDocumentUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
        this.patronRepository = patronRepository;
    }

    @Override
    protected @NonNull Collection<Class<? extends BaseUser>> getAllowedUserClasses() {
        return List.of(Patron.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PatronNotFoundException, DocumentNotFoundException, DocumentNotBorrowedException {
        val patronAuthenticationToken = getAuthenticationTokenFromRepository();
        ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patron = getPatronFromAuthenticationToken(patronAuthenticationToken);

        val documentId = request.getDocumentId();
        val document = getDocumentFromId(documentId);

        ensureDocumentIsAlreadyBorrowed(patron, documentId);
        markDocumentAsReturnedByPatron(patron, document);

        this.documentRepository.save(document);
        this.patronRepository.save(patron);
    }

    private @NonNull Patron getPatronFromAuthenticationToken(@NonNull AuthenticationToken staffAuthenticationToken) throws AuthenticationTokenInvalidException, PatronNotFoundException {
        val patronId = this.authenticationTokenGenerator.getUserIdFromAuthenticationToken(staffAuthenticationToken);

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

    private static void ensureDocumentIsAlreadyBorrowed(@NonNull Patron patron, Document.@NonNull Id documentId) throws DocumentNotBorrowedException {
        val patronAudit = patron.getAudit();
        val borrowedDocumentIdsOfPatron = patronAudit.getBorrowedDocumentIds();

        if (!borrowedDocumentIdsOfPatron.contains(documentId))
            throw new DocumentNotBorrowedException();
    }

    private static void markDocumentAsReturnedByPatron(@NonNull Patron patron, @NonNull Document document) {
        val patronId = patron.getId();
        val documentId = document.getId();

        val patronAudit = patron.getAudit();
        val borrowedDocumentIdsOfPatron = patronAudit.getBorrowedDocumentIds();
        borrowedDocumentIdsOfPatron.remove(documentId);

        val documentAudit = document.getAudit();
        val borrowingPatronIdsOfDocument = documentAudit.getBorrowingPatronIds();
        borrowingPatronIdsOfDocument.remove(patronId);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Document.@NonNull Id documentId;
    }

    public static class PatronNotFoundException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
    public static class DocumentNotBorrowedException extends Exception {}
}
