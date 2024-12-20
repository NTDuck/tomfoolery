package org.tomfoolery.core.usecases.external.staff.documents.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.usecases.external.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.dataproviders.providers.io.file.FileVerifier;

import java.time.Instant;
import java.util.Set;

public final class UpdateDocumentCoverImageUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<UpdateDocumentCoverImageUseCase.Request> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull FileVerifier fileVerifier;

    public static @NonNull UpdateDocumentCoverImageUseCase of(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileVerifier fileVerifier) {
        return new UpdateDocumentCoverImageUseCase(documentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileVerifier);
    }

    private UpdateDocumentCoverImageUseCase(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileVerifier fileVerifier) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
        this.fileVerifier = fileVerifier;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Staff.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentISBNInvalidException, DocumentNotFoundException, DocumentCoverImageInvalidException {
        val staffAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(staffAuthenticationToken);
        val staffId = this.getUserIdFromAuthenticationToken(staffAuthenticationToken);

        val documentISBN = request.getDocumentISBN();
        val documentId = this.getDocumentIdFromISBN(documentISBN);
        val document = this.getDocumentById(documentId);

        val newDocumentCoverImage = request.getNewDocumentCoverImage();
        this.ensureDocumentCoverImageIsValid(newDocumentCoverImage);

        document.setCoverImage(newDocumentCoverImage);
        this.markDocumentAsLastModifiedByStaff(document, staffId);

        this.documentRepository.save(document);
    }

    private Document.@NonNull Id getDocumentIdFromISBN(@NonNull String documentISBN) throws DocumentISBNInvalidException {
        val documentId = Document.Id.of(documentISBN);

        if (documentId == null)
            throw new DocumentISBNInvalidException();

        return documentId;
    }

    private @NonNull Document getDocumentById(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        val document = this.documentRepository.getById(documentId);

        if (document == null)
            throw new DocumentNotFoundException();

        return document;
    }

    private void ensureDocumentCoverImageIsValid(Document.@NonNull CoverImage documentCoverImage) throws DocumentCoverImageInvalidException {
        if (!this.fileVerifier.isImage(documentCoverImage.getBytes()))
            throw new DocumentCoverImageInvalidException();
    }

    private void markDocumentAsLastModifiedByStaff(@NonNull Document document, Staff.@NonNull Id staffId) {
        val documentAudit = document.getAudit();
        val documentAuditTimestamps = documentAudit.getTimestamps();

        documentAudit.setLastModifiedByStaffId(staffId);
        documentAuditTimestamps.setLastModified(Instant.now());
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String documentISBN;
        Document.@NonNull CoverImage newDocumentCoverImage;
    }

    public static class DocumentISBNInvalidException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
    public static class DocumentCoverImageInvalidException extends Exception {}
}
