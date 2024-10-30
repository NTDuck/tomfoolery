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
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

@RequiredArgsConstructor(staticName = "of")
public class RemoveDocumentUseCase implements ThrowableConsumer<RemoveDocumentUseCase.Request> {
    private final @NonNull DocumentRepository documentRepository;

    private final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator;
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository;

    @Override
    public void accept(@NonNull Request request) throws StaffAuthenticationTokenNotFoundException, StaffAuthenticationTokenInvalidException, DocumentNotFoundException {
        val documentId = request.getDocumentId();

        val staffAuthenticationToken = getStaffAuthenticationTokenFromRepository();
        ensureStaffAuthenticationTokenIsValid(staffAuthenticationToken);

        ensureDocumentExists(documentId);

        this.documentRepository.delete(documentId);
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

    private void ensureDocumentExists(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        if (!this.documentRepository.contains(documentId))
            throw new DocumentNotFoundException();
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Document.@NonNull Id documentId;
    }

    public static class StaffAuthenticationTokenNotFoundException extends Exception {}
    public static class StaffAuthenticationTokenInvalidException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
}
