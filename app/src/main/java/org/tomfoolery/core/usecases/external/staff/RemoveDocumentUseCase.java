package org.tomfoolery.core.usecases.external.staff;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.domain.Staff;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

@RequiredArgsConstructor(staticName = "of")
public class RemoveDocumentUseCase implements ThrowableConsumer<RemoveDocumentUseCase.Request> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull AuthenticationTokenService authenticationTokenService;

    @Override
    public void accept(@NonNull Request request) throws StaffAuthenticationTokenInvalidException, DocumentNotFoundException {
        val staffAuthenticationToken = request.getStaffAuthenticationToken();
        val documentId = request.getDocumentId();

        ensureStaffAuthenticationTokenIsValid(staffAuthenticationToken);
        ensureDocumentExists(documentId);

        this.documentRepository.delete(documentId);
    }

    private void ensureStaffAuthenticationTokenIsValid(@NonNull AuthenticationToken staffAuthenticationToken) throws StaffAuthenticationTokenInvalidException {
        if (!this.authenticationTokenService.verifyToken(staffAuthenticationToken, Staff.class))
            throw new StaffAuthenticationTokenInvalidException();
    }

    private void ensureDocumentExists(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        if (!this.documentRepository.contains(documentId))
            throw new DocumentNotFoundException();
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull AuthenticationToken staffAuthenticationToken;
        Document.@NonNull Id documentId;
    }

    public static class StaffAuthenticationTokenInvalidException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
}
