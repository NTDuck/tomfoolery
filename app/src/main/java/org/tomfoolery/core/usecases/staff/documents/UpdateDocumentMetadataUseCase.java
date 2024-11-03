package org.tomfoolery.core.usecases.staff.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.documents.DocumentRepository;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public final class UpdateDocumentMetadataUseCase extends AuthenticatedUserUseCase implements ThrowableFunction<UpdateDocumentMetadataUseCase.Request, UpdateDocumentMetadataUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    public static @NonNull UpdateDocumentMetadataUseCase of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        return new UpdateDocumentMetadataUseCase(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    private UpdateDocumentMetadataUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);
        this.documentRepository = documentRepository;
    }

    @Override
    protected @NonNull Collection<Class<? extends BaseUser>> getAllowedUserClasses() {
        return List.of(Staff.class);
    }

    @Override
    public @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentNotFoundException {
        val staffAuthenticationToken = getAuthenticationTokenFromRepository();
        ensureAuthenticationTokenIsValid(staffAuthenticationToken);
        val staffId = getStaffIdFromAuthenticationToken(staffAuthenticationToken);

        val documentId = request.getDocumentId();
        val newDocumentMetadata = request.getNewDocumentMetadata();

        val document = getDocumentById(documentId);

        updateDocumentMetadataAndMarkAsLastModifiedByStaff(document, newDocumentMetadata, staffId);

        this.documentRepository.save(document);
        return Response.of(documentId);
    }

    private Staff.@NonNull Id getStaffIdFromAuthenticationToken(@NonNull AuthenticationToken staffAuthenticationToken) throws AuthenticationTokenInvalidException {
        val staffId = this.authenticationTokenGenerator.getUserIdFromAuthenticationToken(staffAuthenticationToken);

        if (staffId == null)
            throw new AuthenticationTokenInvalidException();

        return staffId;
    }

    private @NonNull Document getDocumentById(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        val document = this.documentRepository.getById(documentId);

        if (document == null)
            throw new DocumentNotFoundException();

        return document;
    }

    private static void updateDocumentMetadataAndMarkAsLastModifiedByStaff(@NonNull Document document, Document.@NonNull Metadata newDocumentMetadata, Staff.@NonNull Id staffId) {
        document.setMetadata(newDocumentMetadata);

        val documentAudit = document.getAudit();
        val documentAuditTimestamps = documentAudit.getTimestamps();

        documentAudit.setLastModifiedByStaffId(staffId);
        documentAuditTimestamps.setLastModified(Instant.now());
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Document.@NonNull Id documentId;
        Document.@NonNull Metadata newDocumentMetadata;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        Document.@NonNull Id documentId;
    }

    public static class DocumentNotFoundException extends Exception {}
}
