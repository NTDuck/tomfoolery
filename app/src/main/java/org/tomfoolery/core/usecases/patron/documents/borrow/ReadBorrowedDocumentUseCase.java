package org.tomfoolery.core.usecases.patron.documents.borrow;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingRecordRepository;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.relations.DocumentContent;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

import java.util.Set;

public final class ReadBorrowedDocumentUseCase extends AuthenticatedUserUseCase implements ThrowableFunction<ReadBorrowedDocumentUseCase.Request, ReadBorrowedDocumentUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull DocumentContentRepository documentContentRepository;
    private final @NonNull BorrowingRecordRepository borrowingRecordRepository;

    public static @NonNull ReadBorrowedDocumentUseCase of(@NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull BorrowingRecordRepository borrowingRecordRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ReadBorrowedDocumentUseCase(documentRepository, documentContentRepository, borrowingRecordRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ReadBorrowedDocumentUseCase(@NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull BorrowingRecordRepository borrowingRecordRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
        this.documentContentRepository = documentContentRepository;
        this.borrowingRecordRepository = borrowingRecordRepository;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Patron.class);
    }

    @Override
    public @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentISBNInvalidException, DocumentNotFoundException, DocumentNotBorrowedException, DocumentContentNotFoundException {
        val patronAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patronId = this.getUserIdFromAuthenticationToken(patronAuthenticationToken);

        val documentISBN = request.getDocumentISBN();
        val documentId = this.getDocumentIdFromISBN(documentISBN);

        this.ensureDocumentExists(documentId);
        this.ensureDocumentIsAlreadyBorrowed(documentId, patronId);

        val documentContent = this.getDocumentContentByDocumentId(documentId);

        return Response.of(documentContent);
    }

    private Document.@NonNull Id getDocumentIdFromISBN(@NonNull String documentISBN) throws DocumentISBNInvalidException {
        val documentId = Document.Id.of(documentISBN);

        if (documentId == null)
            throw new DocumentISBNInvalidException();

        return documentId;
    }

    private void ensureDocumentExists(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        if (!this.documentRepository.contains(documentId))
            throw new DocumentNotFoundException();
    }

    private void ensureDocumentIsAlreadyBorrowed(Document.@NonNull Id documentId, Patron.@NonNull Id patronId) throws DocumentNotFoundException {
        if (!this.borrowingRecordRepository.containsCurrentlyBorrowedRecord(documentId, patronId))
            throw new DocumentNotFoundException();
    }

    private @NonNull DocumentContent getDocumentContentByDocumentId(Document.@NonNull Id documentId) throws DocumentContentNotFoundException {
        val documentContentId = DocumentContent.Id.of(documentId);
        val documentContent = this.documentContentRepository.getById(documentContentId);

        if (documentContent == null)
            throw new DocumentContentNotFoundException();

        return documentContent;
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String documentISBN;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull DocumentContent documentContent;
    }

    public static class DocumentISBNInvalidException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
    public static class DocumentNotBorrowedException extends Exception {}
    public static class DocumentContentNotFoundException extends Exception {}
}
