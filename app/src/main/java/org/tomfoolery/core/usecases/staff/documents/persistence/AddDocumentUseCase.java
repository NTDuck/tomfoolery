package org.tomfoolery.core.usecases.staff.documents.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.relations.DocumentContent;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.dataproviders.providers.io.file.FileVerifier;
import org.tomfoolery.core.utils.helpers.verifiers.StringVerifier;

import java.time.Instant;
import java.util.Set;

public final class AddDocumentUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<AddDocumentUseCase.Request> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull DocumentContentRepository documentContentRepository;

    private final @NonNull FileVerifier fileVerifier;

    public static @NonNull AddDocumentUseCase of(@NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileVerifier fileVerifier) {
        return new AddDocumentUseCase(documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileVerifier);
    }

    private AddDocumentUseCase(@NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileVerifier fileVerifier) {
        super(authenticationTokenGenerator, authenticationTokenRepository);
        
        this.documentRepository = documentRepository;
        this.documentContentRepository = documentContentRepository;

        this.fileVerifier = fileVerifier;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Staff.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentISBNInvalidException, TitleInvalidException, DescriptionInvalidException, AuthorInvalidException, GenreInvalidException, PublisherInvalidException, DocumentCoverImageInvalidException, DocumentContentInvalidException, DocumentAlreadyExistsException {
        val staffAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(staffAuthenticationToken);
        val staffId = this.getUserIdFromAuthenticationToken(staffAuthenticationToken);

        val documentISBN = request.getDocumentISBN();
        val documentId = this.getDocumentIdFromISBN(documentISBN);
        this.ensureDocumentDoesNotExist(documentId);

        val documentMetadata = request.getDocumentMetadata();
        this.ensureDocumentMetadataIsValid(documentMetadata);

        val documentCoverImage = request.getDocumentCoverImage();
        this.ensureDocumentCoverImageIsValid(documentCoverImage);

        val rawDocumentContent = request.getDocumentContent();
        val documentContent = DocumentContent.of(DocumentContent.Id.of(documentId), rawDocumentContent);
        this.ensureDocumentContentIsValid(documentContent);

        this.documentContentRepository.save(documentContent);

        val document = this.createDocumentAndMarkAsCreatedByStaff(documentId, documentCoverImage, documentMetadata, staffId);
        this.documentRepository.save(document);
    }

    private Document.@NonNull Id getDocumentIdFromISBN(@NonNull String documentISBN) throws DocumentISBNInvalidException {
        val documentId = Document.Id.of(documentISBN);

        if (documentId == null)
            throw new DocumentISBNInvalidException();

        return documentId;
    }

    private void ensureDocumentMetadataIsValid(Document.@NonNull Metadata documentMetadata) throws TitleInvalidException, DescriptionInvalidException, AuthorInvalidException, GenreInvalidException, PublisherInvalidException {
        if (!StringVerifier.verify(documentMetadata.getTitle()))
            throw new TitleInvalidException();

        if (!StringVerifier.verify(documentMetadata.getDescription()))
            throw new DescriptionInvalidException();

        documentMetadata.getAuthors().forEach(author -> {
            if (!StringVerifier.verify(author))
                throw new AuthorInvalidException();
        });

        documentMetadata.getGenres().forEach(genre -> {
            if (!StringVerifier.verify(genre))
                throw new GenreInvalidException();
        });

        if (!StringVerifier.verify(documentMetadata.getPublisher()))
            throw new PublisherInvalidException();
    }

    private void ensureDocumentContentIsValid(@NonNull DocumentContent documentContent) throws DocumentContentInvalidException {
        if (!this.fileVerifier.isDocument(documentContent.getBytes()))
            throw new DocumentContentInvalidException();
    }

    private void ensureDocumentCoverImageIsValid(Document.@NonNull CoverImage documentCoverImage) throws DocumentCoverImageInvalidException {
        if (!this.fileVerifier.isImage(documentCoverImage.getBytes()))
            throw new DocumentCoverImageInvalidException();
    }

    private void ensureDocumentDoesNotExist(Document.@NonNull Id documentId) throws DocumentAlreadyExistsException {
        if (this.documentRepository.contains(documentId))
            throw new DocumentAlreadyExistsException();
    }

    private @NonNull Document createDocumentAndMarkAsCreatedByStaff(Document.@NonNull Id documentId, Document.@NonNull CoverImage documentCoverImage, Document.@NonNull Metadata documentMetadata, Staff.@NonNull Id staffId) {
        val documentAuditTimestamps = Document.Audit.Timestamps.of(Instant.now());
        val documentAudit = Document.Audit.of(documentAuditTimestamps, staffId);

        return Document.of(documentId, documentAudit, documentMetadata, null, documentCoverImage);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String documentISBN;

        Document.@NonNull CoverImage documentCoverImage;
        byte @NonNull [] documentContent;
        Document.@NonNull Metadata documentMetadata;
    }

    public static class DocumentISBNInvalidException extends Exception {}

    public static class TitleInvalidException extends DocumentMetadataInvalidException {}
    public static class DescriptionInvalidException extends DocumentMetadataInvalidException {}
    public static class AuthorInvalidException extends DocumentMetadataInvalidException {}
    public static class GenreInvalidException extends DocumentMetadataInvalidException {}
    public static class PublisherInvalidException extends DocumentMetadataInvalidException {}

    public static class DocumentMetadataInvalidException extends RuntimeException {}
    public static class DocumentContentInvalidException extends Exception {}
    public static class DocumentCoverImageInvalidException extends Exception {}
    public static class DocumentAlreadyExistsException extends Exception {}
}
