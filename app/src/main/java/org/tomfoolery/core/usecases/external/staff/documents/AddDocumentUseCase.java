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

@RequiredArgsConstructor(staticName = "of")
public class AddDocumentUseCase implements ThrowableFunction<AddDocumentUseCase.Request, AddDocumentUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    private final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator;
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository;

    @Override
    public @NonNull Response apply(@NonNull Request request) throws StaffAuthenticationTokenNotFoundException, StaffAuthenticationTokenInvalidException, DocumentAlreadyExistsException {
        val documentId = request.getDocumentId();
        val documentMetadata = request.getDocumentMetadata();

        val staffAuthenticationToken = getStaffAuthenticationTokenFromRepository();
        ensureStaffAuthenticationTokenIsValid(staffAuthenticationToken);
        val staffId = getStaffIdFromAuthenticationToken(staffAuthenticationToken);

        ensureDocumentDoesNotExist(documentId);

        val document = createDocumentAndMarkAsCreatedByStaff(documentId, documentMetadata, staffId);
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

    private void ensureDocumentDoesNotExist(Document.@NonNull Id documentId) throws DocumentAlreadyExistsException {
        if (this.documentRepository.contains(documentId))
            throw new DocumentAlreadyExistsException();
    }

    private @NonNull Document createDocumentAndMarkAsCreatedByStaff(Document.@NonNull Id documentId, Document.@NonNull Metadata documentMetadata, Staff.@NonNull Id staffId) {
        val documentAudit = Document.Audit.of(staffId);
        return Document.of(documentId, documentMetadata, documentAudit);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Document.@NonNull Id documentId;
        Document.@NonNull Metadata documentMetadata;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        Document.@NonNull Id documentId;
    }

    public static class StaffAuthenticationTokenNotFoundException extends Exception {}
    public static class StaffAuthenticationTokenInvalidException extends Exception {}
    public static class DocumentAlreadyExistsException extends Exception {}
}
