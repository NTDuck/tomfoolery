package org.tomfoolery.core.usecases.patron.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.auth.security.AuthenticationToken;

import java.util.Set;

public final class BorrowDocumentUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<BorrowDocumentUseCase.Request> {
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
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PatronNotFoundException, DocumentNotFoundException, DocumentAlreadyBorrowedException {
        val patronAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patron = this.getPatronFromAuthenticationToken(patronAuthenticationToken);

        val documentId = request.getDocumentId();
        val fragmentaryDocument = this.getFragmentaryDocumentFromId(documentId);

        this.ensureDocumentIsNotBorrowed(patron, documentId);
        this.markDocumentAsBorrowedByPatron(patron, fragmentaryDocument);

        this.documentRepository.saveFragment(fragmentaryDocument);
        this.patronRepository.save(patron);
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

    private @NonNull FragmentaryDocument getFragmentaryDocumentFromId(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        val fragmentaryDocument = this.documentRepository.getByIdWithoutContent(documentId);

        if (fragmentaryDocument == null)
            throw new DocumentNotFoundException();

        return fragmentaryDocument;
    }

    private void ensureDocumentIsNotBorrowed(@NonNull Patron patron, Document.@NonNull Id documentId) throws DocumentAlreadyBorrowedException {
        val borrowedDocumentIds = patron.getAudit().getBorrowedDocumentIds();

        if (borrowedDocumentIds.contains(documentId))
            throw new DocumentAlreadyBorrowedException();
    }

    private void markDocumentAsBorrowedByPatron(@NonNull Patron patron, @NonNull FragmentaryDocument fragmentaryDocument) {
        val patronId = patron.getId();
        val documentId = fragmentaryDocument.getId();

        val borrowedDocumentIds = patron.getAudit().getBorrowedDocumentIds();
        borrowedDocumentIds.add(documentId);

        val borrowingPatronIds = fragmentaryDocument.getAudit().getBorrowingPatronIds();
        borrowingPatronIds.add(patronId);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Document.@NonNull Id documentId;
    }

    public static class PatronNotFoundException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
    public static class DocumentAlreadyBorrowedException extends Exception {}
}
