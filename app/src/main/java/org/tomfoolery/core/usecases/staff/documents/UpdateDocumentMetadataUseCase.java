package org.tomfoolery.core.usecases.staff.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public final class UpdateDocumentMetadataUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<UpdateDocumentMetadataUseCase.Request> {
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
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentNotFoundException {
        val staffAuthenticationToken = getAuthenticationTokenFromRepository();
        ensureAuthenticationTokenIsValid(staffAuthenticationToken);
        val staffId = getStaffIdFromAuthenticationToken(staffAuthenticationToken);

        val documentId = request.getDocumentId();
        val newDocumentMetadata = request.getNewDocumentMetadata();

        val fragmentaryDocument = getFragmentaryDocumentById(documentId);

        updateDocumentMetadataAndMarkAsLastModifiedByStaff(fragmentaryDocument, newDocumentMetadata, staffId);

        this.documentRepository.save(fragmentaryDocument);
    }

    private Staff.@NonNull Id getStaffIdFromAuthenticationToken(@NonNull AuthenticationToken staffAuthenticationToken) throws AuthenticationTokenInvalidException {
        val staffId = this.authenticationTokenGenerator.getUserIdFromAuthenticationToken(staffAuthenticationToken);

        if (staffId == null)
            throw new AuthenticationTokenInvalidException();

        return staffId;
    }

    private @NonNull FragmentaryDocument getFragmentaryDocumentById(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        val fragmentaryDocument = this.documentRepository.getFragmentaryById(documentId);

        if (fragmentaryDocument == null)
            throw new DocumentNotFoundException();

        return fragmentaryDocument;
    }

    private static void updateDocumentMetadataAndMarkAsLastModifiedByStaff(@NonNull FragmentaryDocument fragmentaryDocument, Document.@NonNull Metadata newDocumentMetadata, Staff.@NonNull Id staffId) {
        fragmentaryDocument.setMetadata(newDocumentMetadata);

        val documentAudit = fragmentaryDocument.getAudit();
        val documentAuditTimestamps = documentAudit.getTimestamps();

        documentAudit.setLastModifiedByStaffId(staffId);
        documentAuditTimestamps.setLastModified(Instant.now());
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Document.@NonNull Id documentId;
        Document.@NonNull Metadata newDocumentMetadata;
    }

    public static class DocumentNotFoundException extends Exception {}
}
