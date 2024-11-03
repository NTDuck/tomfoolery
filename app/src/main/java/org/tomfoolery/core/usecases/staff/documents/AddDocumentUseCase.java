package org.tomfoolery.core.usecases.staff.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public final class AddDocumentUseCase extends AuthenticatedUserUseCase implements ThrowableFunction<AddDocumentUseCase.Request, AddDocumentUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    public static @NonNull AddDocumentUseCase of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        return new AddDocumentUseCase(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    private AddDocumentUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);
        this.documentRepository = documentRepository;
    }

    @Override
    protected @NonNull Collection<Class<? extends BaseUser>> getAllowedUserClasses() {
        return List.of(Staff.class);
    }

    @Override
    public @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentAlreadyExistsException {
        val staffAuthenticationToken = getAuthenticationTokenFromRepository();
        ensureAuthenticationTokenIsValid(staffAuthenticationToken);
        val staffId = getStaffIdFromAuthenticationToken(staffAuthenticationToken);

        val documentId = request.getDocumentId();
        ensureDocumentDoesNotExist(documentId);

        val documentContent = request.getDocumentContent();
        val documentMetadata = request.getDocumentMetadata();

        val document = createDocumentAndMarkAsCreatedByStaff(documentId, documentContent, documentMetadata, staffId);

        this.documentRepository.save(document);
        return Response.of(documentId);
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

    private static @NonNull Document createDocumentAndMarkAsCreatedByStaff(Document.@NonNull Id documentId, Document.@NonNull Content documentContent, Document.@NonNull Metadata documentMetadata, Staff.@NonNull Id staffId) {
        val documentAuditTimestamps = Document.Audit.Timestamps.of(Instant.now());
        val documentAudit = Document.Audit.of(staffId, documentAuditTimestamps);

        return Document.of(documentId, documentContent, documentMetadata, documentAudit);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Document.@NonNull Id documentId;
        Document.@NonNull Content documentContent;
        Document.@NonNull Metadata documentMetadata;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        Document.@NonNull Id documentId;
    }

    public static class DocumentAlreadyExistsException extends Exception {}
}
