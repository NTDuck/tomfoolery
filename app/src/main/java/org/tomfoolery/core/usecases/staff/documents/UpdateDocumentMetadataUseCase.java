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
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.auth.security.AuthenticationToken;

import java.time.Instant;
import java.util.Set;

public final class UpdateDocumentMetadataUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<UpdateDocumentMetadataUseCase.Request> {
    private final @NonNull DocumentRepository documentRepository;

    public static @NonNull UpdateDocumentMetadataUseCase of(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new UpdateDocumentMetadataUseCase(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private UpdateDocumentMetadataUseCase(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Staff.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentNotFoundException {
        val staffAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(staffAuthenticationToken);
        val staffId = this.getStaffIdFromAuthenticationToken(staffAuthenticationToken);

        val documentId = request.getDocumentId();
        val newDocumentMetadata = request.getNewDocumentMetadata();

        val fragmentaryDocument = this.getFragmentaryDocumentById(documentId);

        this.updateDocumentMetadataAndMarkAsLastModifiedByStaff(fragmentaryDocument, newDocumentMetadata, staffId);

        this.documentRepository.saveFragment(fragmentaryDocument);
    }

    private Staff.@NonNull Id getStaffIdFromAuthenticationToken(@NonNull AuthenticationToken staffAuthenticationToken) throws AuthenticationTokenInvalidException {
        val staffId = this.authenticationTokenGenerator.getUserIdFromAuthenticationToken(staffAuthenticationToken);

        if (staffId == null)
            throw new AuthenticationTokenInvalidException();

        return staffId;
    }

    private @NonNull FragmentaryDocument getFragmentaryDocumentById(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        val fragmentaryDocument = this.documentRepository.getByIdWithoutContent(documentId);

        if (fragmentaryDocument == null)
            throw new DocumentNotFoundException();

        return fragmentaryDocument;
    }

    private void updateDocumentMetadataAndMarkAsLastModifiedByStaff(@NonNull FragmentaryDocument fragmentaryDocument, Document.@NonNull Metadata newDocumentMetadata, Staff.@NonNull Id staffId) {
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
