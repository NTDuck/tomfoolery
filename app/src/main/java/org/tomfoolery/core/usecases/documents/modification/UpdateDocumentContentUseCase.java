package org.tomfoolery.core.usecases.documents.modification;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

import java.time.Instant;
import java.util.Set;

public final class UpdateDocumentContentUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<UpdateDocumentContentUseCase.Request> {
    private final @NonNull DocumentRepository documentRepository;

    public static @NonNull UpdateDocumentContentUseCase of(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new UpdateDocumentContentUseCase(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private UpdateDocumentContentUseCase(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Staff.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentISBNInvalidException, DocumentNotFoundException {
        val staffAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(staffAuthenticationToken);
        val staffId = this.getUserIdFromAuthenticationToken(staffAuthenticationToken);

        val documentISBN = request.getDocumentISBN();
        val documentId = this.getDocumentIdFromISBN(documentISBN);
        val document = this.getDocumentById(documentId);

        val newDocumentContent = request.getNewDocumentContent();
        this.updateDocumentContentAndMarkAsLastModifiedByStaff(document, newDocumentContent, staffId);

        this.documentRepository.save(document);
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

    private void updateDocumentContentAndMarkAsLastModifiedByStaff(@NonNull Document document, Document.@NonNull Content newDocumentContent, Staff.@NonNull Id staffId) {
        document.setContent(newDocumentContent);

        val documentAudit = document.getAudit();
        val documentAuditTimestamps = documentAudit.getTimestamps();

        documentAudit.setLastModifiedByStaffId(staffId);
        documentAuditTimestamps.setLastModified(Instant.now());
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String documentISBN;
        Document.@NonNull Content newDocumentContent;
    }

    public static class DocumentISBNInvalidException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
}
