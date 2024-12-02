package org.tomfoolery.core.usecases.patron.documents.borrow;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingRecordRepository;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.relations.BorrowingRecord;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

import java.time.Instant;
import java.util.Set;

public final class ReturnDocumentUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<ReturnDocumentUseCase.Request> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull BorrowingRecordRepository borrowingRecordRepository;

    public static @NonNull ReturnDocumentUseCase of(@NonNull DocumentRepository documentRepository, @NonNull BorrowingRecordRepository borrowingRecordRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ReturnDocumentUseCase(documentRepository, borrowingRecordRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ReturnDocumentUseCase(@NonNull DocumentRepository documentRepository, @NonNull BorrowingRecordRepository borrowingRecordRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
        this.borrowingRecordRepository = borrowingRecordRepository;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Patron.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentISBNInvalidException, DocumentNotFoundException, DocumentNotBorrowedException {
        val patronAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patronId = this.getUserIdFromAuthenticationToken(patronAuthenticationToken);

        val documentISBN = request.getDocumentISBN();
        val documentId = this.getDocumentIdFromISBN(documentISBN);

        val borrowingRecord = this.getMostRecentlyAndCurrentlyBorrowedRecord(documentId, patronId);
        this.markBorrowingRecordAsReturned(borrowingRecord);
        this.borrowingRecordRepository.save(borrowingRecord);
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

    private @NonNull BorrowingRecord getMostRecentlyAndCurrentlyBorrowedRecord(Document.@NonNull Id documentId, Patron.@NonNull Id patronId) throws DocumentNotBorrowedException {
        val borrowingRecord = this.borrowingRecordRepository.getMostRecentlyAndCurrentlyBorrowedRecord(documentId, patronId);

        if (borrowingRecord == null)
            throw new DocumentNotBorrowedException();

        return borrowingRecord;
    }

    private void markBorrowingRecordAsReturned(@NonNull BorrowingRecord borrowingRecord) {
        val currentTimestamp = Instant.now();
        borrowingRecord.setReturnedTimestamp(currentTimestamp);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String documentISBN;
    }

    public static class DocumentISBNInvalidException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
    public static class DocumentNotBorrowedException extends Exception {}
}
