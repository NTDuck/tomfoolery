package org.tomfoolery.core.usecases.external.staff;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.domain.Staff;
import org.tomfoolery.core.domain.auth.AuthenticationToken;
import org.tomfoolery.core.utils.functional.ThrowableFunction;

import java.time.LocalDateTime;

@RequiredArgsConstructor(staticName = "of")
public class UpdateDocumentUseCase implements ThrowableFunction<UpdateDocumentUseCase.Request, UpdateDocumentUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull AuthenticationTokenService authenticationTokenService;

    @Override
    public @NonNull Response apply(@NonNull Request request)
            throws StaffAuthenticationTokenInvalidException, DocumentNotFoundException {
        val staffAuthenticationToken = request.getStaffAuthenticationToken();

        val documentId = request.getDocumentId();
        val documentMetadata = request.getDocumentMetadata();

        ensureStaffAuthenticationTokenIsValid(staffAuthenticationToken);
        val staffId = getStaffIdFromAuthenticationToken(staffAuthenticationToken);

        val document = getDocumentById(documentId);
        document.setMetadata(documentMetadata);
        markDocumentAsLastModifiedByStaff(document, staffId);

        this.documentRepository.save(document);
        return Response.of(documentId);
    }

    private void ensureStaffAuthenticationTokenIsValid(@NonNull AuthenticationToken staffAuthenticationToken)
            throws StaffAuthenticationTokenInvalidException {
        if (!this.authenticationTokenService.verifyToken(staffAuthenticationToken, Staff.class))
            throw new StaffAuthenticationTokenInvalidException();
    }

    private Staff.@NonNull Id getStaffIdFromAuthenticationToken(@NonNull AuthenticationToken staffAuthenticationToken)
            throws StaffAuthenticationTokenInvalidException {
        val staffId = this.authenticationTokenService.getUserIdFromToken(staffAuthenticationToken);

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

    public static class StaffAuthenticationTokenInvalidException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
}
