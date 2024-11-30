package org.tomfoolery.core.usecases.documents.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.documents.DocumentWithoutContent;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

import java.util.Set;

public final class RemoveDocumentUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<RemoveDocumentUseCase.Request> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull PatronRepository patronRepository;

    public static @NonNull RemoveDocumentUseCase of(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new RemoveDocumentUseCase(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private RemoveDocumentUseCase(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);
        
        this.documentRepository = documentRepository;
        this.patronRepository = patronRepository;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Staff.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentISBNInvalidException, DocumentNotFoundException {
        val staffAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(staffAuthenticationToken);

        val documentISBN = request.getDocumentISBN();
        val documentId = this.getDocumentIdFromISBN(documentISBN);
        val documentWithoutContent = this.getDocumentWithoutContentById(documentId);

        this.cascadeRemoveDocumentFromBorrowingPatrons(documentWithoutContent);

        this.documentRepository.delete(documentId);
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

    private void cascadeRemoveDocumentFromBorrowingPatrons(@NonNull DocumentWithoutContent documentWithoutContent) {
        val documentId = documentWithoutContent.getId();
        val borrowingPatronIds = documentWithoutContent.getAudit().getBorrowingPatronIds();

        for (val borrowingPatronId : borrowingPatronIds)
            this.removeDocumentFromBorrowingPatron(documentId, borrowingPatronId);
    }

    private void removeDocumentFromBorrowingPatron(Document.@NonNull Id documentId, Patron.@NonNull Id borrowingPatronId) {
        val borrowingPatron = this.patronRepository.getById(borrowingPatronId);

        if (borrowingPatron == null)
            return;

        val borrowedDocumentIds = borrowingPatron.getAudit().getBorrowedDocumentIds();
        borrowedDocumentIds.remove(documentId);

        this.patronRepository.save(borrowingPatron);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String documentISBN;
    }

    public static class DocumentISBNInvalidException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
}
