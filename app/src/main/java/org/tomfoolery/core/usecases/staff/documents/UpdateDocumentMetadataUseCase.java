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
import org.tomfoolery.core.utils.helpers.verifiers.StringVerifier;

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
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentISBNInvalidException, TitleInvalidException, DescriptionInvalidException, AuthorInvalidException, GenreInvalidException, PublisherInvalidException, DocumentNotFoundException {
        val staffAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(staffAuthenticationToken);
        val staffId = this.getUserIdFromAuthenticationToken(staffAuthenticationToken);

        val documentISBN = request.getDocumentISBN();
        val documentId = this.getDocumentIdFromISBN(documentISBN);
        val document = this.getDocumentById(documentId);

        val newDocumentMetadata = request.getNewDocumentMetadata();
        this.ensureDocumentMetadataIsValid(newDocumentMetadata);

        this.updateDocumentMetadataAndMarkAsLastModifiedByStaff(document, newDocumentMetadata, staffId);

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

    private void updateDocumentMetadataAndMarkAsLastModifiedByStaff(@NonNull Document document, Document.@NonNull Metadata newDocumentMetadata, Staff.@NonNull Id staffId) {
        document.setMetadata(newDocumentMetadata);

        val documentAudit = document.getAudit();
        val documentAuditTimestamps = documentAudit.getTimestamps();

        documentAudit.setLastModifiedByStaffId(staffId);
        documentAuditTimestamps.setLastModified(Instant.now());
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String documentISBN;
        Document.@NonNull Metadata newDocumentMetadata;
    }

    public static class DocumentISBNInvalidException extends Exception {}

    public static class TitleInvalidException extends DocumentMetadataInvalidException {}
    public static class DescriptionInvalidException extends DocumentMetadataInvalidException {}
    public static class AuthorInvalidException extends DocumentMetadataInvalidException {}
    public static class GenreInvalidException extends DocumentMetadataInvalidException {}
    public static class PublisherInvalidException extends DocumentMetadataInvalidException {}

    public static class DocumentMetadataInvalidException extends RuntimeException {}
    public static class DocumentNotFoundException extends Exception {}
}
