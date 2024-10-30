package org.tomfoolery.core.usecases.external.staff.documents;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

import java.time.LocalDateTime;

@RequiredArgsConstructor(staticName = "of")
public class UpdateDocumentUseCase implements ThrowableFunction<UpdateDocumentUseCase.Request, UpdateDocumentUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    private final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator;
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository;

    @Override
    public @NonNull Response apply(@NonNull Request request) throws StaffAuthenticationTokenNotFoundException, StaffAuthenticationTokenInvalidException, DocumentNotFoundException {
        val documentId = request.getDocumentId();
        val documentMetadata = request.getDocumentMetadata();

        val staffAuthenticationToken = getStaffAuthenticationTokenFromRepository();
        ensureStaffAuthenticationTokenIsValid(staffAuthenticationToken);
        val staffId = getStaffIdFromAuthenticationToken(staffAuthenticationToken);

        val document = getDocumentById(documentId);
        document.setMetadata(documentMetadata);
        markDocumentAsLastModifiedByStaff(document, staffId);

        this.documentRepository.save(document);
        return Response.of(documentId);
    }

    private @NonNull AuthenticationToken getStaffAuthenticationTokenFromRepository() throws StaffAuthenticationTokenNotFoundException {
        val staffAuthenticationToken = this.authenticationTokenRepository.get();

        if (staffAuthenticationToken == null)
            throw new StaffAuthenticationTokenNotFoundException();

        return staffAuthenticationToken;
    }

    private void ensureStaffAuthenticationTokenIsValid(@NonNull AuthenticationToken staffAuthenticationToken) throws StaffAuthenticationTokenInvalidException {
        if (!this.authenticationTokenGenerator.verifyAuthenticationToken(staffAuthenticationToken, Staff.class))
            throw new StaffAuthenticationTokenInvalidException();
    }

    private Staff.@NonNull Id getStaffIdFromAuthenticationToken(@NonNull AuthenticationToken staffAuthenticationToken) throws StaffAuthenticationTokenInvalidException {
        val staffId = this.authenticationTokenGenerator.getUserIdFromAuthenticationToken(staffAuthenticationToken);

        if (staffId == null)
            throw new StaffAuthenticationTokenInvalidException();

        return staffId;
    }

    private @NonNull Document getDocumentById(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        val document = this.documentRepository.getById(documentId);

        if (document == null)
            throw new DocumentNotFoundException();

        return document;
    }

    private void markDocumentAsLastModifiedByStaff(@NonNull Document document, Staff.@NonNull Id staffId) {
        val documentAudit = document.getAudit();
        documentAudit.setLastModifiedByStaffId(staffId);

        val timestamps = documentAudit.getTimestamps();
        timestamps.setLastModified(LocalDateTime.now());
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull AuthenticationToken staffAuthenticationToken;

        Document.@NonNull Id documentId;
        Document.@NonNull Metadata documentMetadata;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        Document.@NonNull Id documentId;
    }

    public static class StaffAuthenticationTokenNotFoundException extends Exception {}
    public static class StaffAuthenticationTokenInvalidException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
}
