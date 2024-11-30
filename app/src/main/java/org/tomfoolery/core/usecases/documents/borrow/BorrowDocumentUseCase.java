package org.tomfoolery.core.usecases.documents.borrow;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.documents.DocumentWithoutContent;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.auth.security.AuthenticationToken;

import java.util.Set;

public final class BorrowDocumentUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<BorrowDocumentUseCase.Request> {
    private static final @Unsigned int MAX_BORROWED_DOCUMENTS_PER_PATRON = 10;

    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull PatronRepository patronRepository;

    public static @NonNull BorrowDocumentUseCase of(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new BorrowDocumentUseCase(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private BorrowDocumentUseCase(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
        this.patronRepository = patronRepository;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Patron.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PatronNotFoundException, DocumentISBNInvalidException, DocumentNotFoundException, DocumentBorrowLimitExceeded, DocumentAlreadyBorrowedException {
        val patronAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patron = this.getPatronFromAuthenticationToken(patronAuthenticationToken);

        val documentISBN = request.getDocumentISBN();
        val documentId = this.getDocumentIdFromISBN(documentISBN);
        val documentWithoutContent = this.getDocumentWithoutContentById(documentId);

        this.ensureDocumentBorrowLimitNotExceeded(patron);
        this.ensureDocumentIsNotBorrowed(patron, documentId);

        this.markDocumentAsBorrowedByPatron(patron, documentWithoutContent);

        this.documentRepository.save(documentWithoutContent);
        this.patronRepository.save(patron);
    }

    private @NonNull Patron getPatronFromAuthenticationToken(@NonNull AuthenticationToken patronAuthenticationToken) throws AuthenticationTokenInvalidException, PatronNotFoundException {
        val patronId = this.getUserIdFromAuthenticationToken(patronAuthenticationToken);
        val patron = this.patronRepository.getById(patronId);

        if (patron == null)
            throw new PatronNotFoundException();

        return patron;
    }

    private Document.@NonNull Id getDocumentIdFromISBN(@NonNull String documentISBN) throws DocumentISBNInvalidException {
        val documentId = Document.Id.of(documentISBN);

        if (documentId == null)
            throw new DocumentISBNInvalidException();

        return documentId;
    }

    private @NonNull DocumentWithoutContent getDocumentWithoutContentById(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        val documentWithoutContent = this.documentRepository.getByIdWithoutContent(documentId);

        if (documentWithoutContent == null)
            throw new DocumentNotFoundException();

        return documentWithoutContent;
    }

    private void ensureDocumentBorrowLimitNotExceeded(@NonNull Patron patron) throws DocumentBorrowLimitExceeded {
        val borrowedDocumentIds = patron.getAudit().getBorrowedDocumentIds();

        if (borrowedDocumentIds.size() >= MAX_BORROWED_DOCUMENTS_PER_PATRON)
            throw new DocumentBorrowLimitExceeded();
    }

    private void ensureDocumentIsNotBorrowed(@NonNull Patron patron, Document.@NonNull Id documentId) throws DocumentAlreadyBorrowedException {
        val borrowedDocumentIds = patron.getAudit().getBorrowedDocumentIds();

        if (borrowedDocumentIds.contains(documentId))
            throw new DocumentAlreadyBorrowedException();
    }

    private void markDocumentAsBorrowedByPatron(@NonNull Patron patron, @NonNull DocumentWithoutContent documentWithoutContent) {
        val patronId = patron.getId();
        val documentId = documentWithoutContent.getId();

        val borrowedDocumentIds = patron.getAudit().getBorrowedDocumentIds();
        borrowedDocumentIds.add(documentId);

        val borrowingPatronIds = documentWithoutContent.getAudit().getBorrowingPatronIds();
        borrowingPatronIds.add(patronId);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String documentISBN;
    }

    public static class DocumentISBNInvalidException extends Exception {}
    public static class PatronNotFoundException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
    public static class DocumentAlreadyBorrowedException extends Exception {}
    public static class DocumentBorrowLimitExceeded extends Exception {}
}
