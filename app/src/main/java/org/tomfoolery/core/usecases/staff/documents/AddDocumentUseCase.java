package org.tomfoolery.core.usecases.staff.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.auth.security.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.documents.AverageRating;

import java.time.Instant;
import java.util.Set;

public final class AddDocumentUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<AddDocumentUseCase.Request> {
    private final @NonNull DocumentRepository documentRepository;

    public static @NonNull AddDocumentUseCase of(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new AddDocumentUseCase(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private AddDocumentUseCase(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);
        
        this.documentRepository = documentRepository;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Staff.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentAlreadyExistsException {
        val staffAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(staffAuthenticationToken);
        val staffId = this.getStaffIdFromAuthenticationToken(staffAuthenticationToken);

        val documentId = request.getDocumentId();
        this.ensureDocumentDoesNotExist(documentId);

        val documentContent = request.getDocumentContent();
        val documentMetadata = request.getDocumentMetadata();

        val document = this.createDocumentAndMarkAsCreatedByStaff(documentId, documentContent, documentMetadata, staffId);

        this.documentRepository.save(document);
    }

    private Staff.@NonNull Id getStaffIdFromAuthenticationToken(@NonNull AuthenticationToken staffAuthenticationToken) throws AuthenticationTokenInvalidException {
        val staffId = this.authenticationTokenGenerator.getUserIdFromAuthenticationToken(staffAuthenticationToken);

        if (staffId == null)
            throw new AuthenticationTokenInvalidException();

        return staffId;
    }

    private void ensureDocumentDoesNotExist(Document.@NonNull Id documentId) throws DocumentAlreadyExistsException {
        if (this.documentRepository.contains(documentId))
            throw new DocumentAlreadyExistsException();
    }

    private @NonNull Document createDocumentAndMarkAsCreatedByStaff(Document.@NonNull Id documentId, Document.@NonNull Content documentContent, Document.@NonNull Metadata documentMetadata, Staff.@NonNull Id staffId) {
        val documentAuditTimestamps = Document.Audit.Timestamps.of(Instant.now());
        val documentRating = AverageRating.of();
        val documentAudit = Document.Audit.of(staffId, documentRating, documentAuditTimestamps);

        return Document.of(documentId, documentContent, documentMetadata, documentAudit);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Document.@NonNull Id documentId;
        Document.@NonNull Content documentContent;
        Document.@NonNull Metadata documentMetadata;
    }

    public static class DocumentAlreadyExistsException extends Exception {}
}
