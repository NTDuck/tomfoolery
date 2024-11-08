package org.tomfoolery.core.usecases.staff.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

import java.util.Collection;
import java.util.List;

public final class RemoveDocumentUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<RemoveDocumentUseCase.Request> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull PatronRepository patronRepository;

    public static @NonNull RemoveDocumentUseCase of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository) {
        return new RemoveDocumentUseCase(authenticationTokenGenerator, authenticationTokenRepository, documentRepository, patronRepository);
    }

    private RemoveDocumentUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);
        
        this.documentRepository = documentRepository;
        this.patronRepository = patronRepository;
    }

    @Override
    protected @NonNull Collection<Class<? extends BaseUser>> getAllowedUserClasses() {
        return List.of(Staff.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentNotFoundException {
        val staffAuthenticationToken = getAuthenticationTokenFromRepository();
        ensureAuthenticationTokenIsValid(staffAuthenticationToken);

        val documentId = request.getDocumentId();
        val fragmentaryDocument = getFragmentaryDocumentById(documentId);

        removeDocumentFromBorrowingPatrons(fragmentaryDocument);

        this.documentRepository.delete(documentId);
    }

    private @NonNull FragmentaryDocument getFragmentaryDocumentById(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        val fragmentaryDocument = this.documentRepository.getFragmentaryById(documentId);

        if (fragmentaryDocument == null)
            throw new DocumentNotFoundException();

        return fragmentaryDocument;
    }

    private void removeDocumentFromBorrowingPatrons(@NonNull FragmentaryDocument fragmentaryDocument) {
        val documentId = fragmentaryDocument.getId();
        val borrowingPatronIds = fragmentaryDocument.getAudit().getBorrowingPatronIds();

        for (val borrowingPatronId : borrowingPatronIds)
            removeDocumentFromBorrowingPatron(documentId, borrowingPatronId);
    }

    private void removeDocumentFromBorrowingPatron(Document.@NonNull Id documentId, Patron.@NonNull Id patronId) {
        val patron = this.patronRepository.getById(patronId);

        if (patron == null)
            return;

        val borrowedDocumentIds = patron.getAudit().getBorrowedDocumentIds();
        borrowedDocumentIds.remove(documentId);

        this.patronRepository.save(patron);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Document.@NonNull Id documentId;
    }

    public static class DocumentNotFoundException extends Exception {}
}
