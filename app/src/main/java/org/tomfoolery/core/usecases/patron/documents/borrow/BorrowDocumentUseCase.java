package org.tomfoolery.core.usecases.patron.documents.borrow;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
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

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

public final class BorrowDocumentUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<BorrowDocumentUseCase.Request> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull BorrowingRecordRepository borrowingRecordRepository;

    private static final @Unsigned int MAX_BORROWED_DOCUMENTS_PER_PATRON = 10;
    private static final @NonNull Duration BORROWING_PERIOD = Duration.ofDays(30);

    public static @NonNull BorrowDocumentUseCase of(@NonNull DocumentRepository documentRepository, @NonNull BorrowingRecordRepository borrowingRecordRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new BorrowDocumentUseCase(documentRepository, borrowingRecordRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private BorrowDocumentUseCase(@NonNull DocumentRepository documentRepository, @NonNull BorrowingRecordRepository borrowingRecordRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
        this.borrowingRecordRepository = borrowingRecordRepository;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Patron.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentISBNInvalidException, DocumentNotFoundException, DocumentBorrowLimitExceeded, DocumentAlreadyBorrowedException {
        val patronAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patronId = this.getUserIdFromAuthenticationToken(patronAuthenticationToken);

        val documentISBN = request.getDocumentISBN();
        val documentId = this.getDocumentIdFromISBN(documentISBN);
        this.ensureDocumentExists(documentId);

        this.ensureDocumentBorrowLimitNotExceeded(patronId);
        this.ensureDocumentNotBorrowed(documentId, patronId);

        val borrowingRecord = this.createBorrowingRecord(documentId, patronId);
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

    private void ensureDocumentBorrowLimitNotExceeded(Patron.@NonNull Id patronId) throws DocumentBorrowLimitExceeded {
        val numberOfCurrentlyBorrowedDocumentsByPatron = this.borrowingRecordRepository.getNumberOfCurrentlyBorrowedDocumentsByPatron(patronId);

        if (numberOfCurrentlyBorrowedDocumentsByPatron >= MAX_BORROWED_DOCUMENTS_PER_PATRON)
            throw new DocumentBorrowLimitExceeded();
    }

    private void ensureDocumentNotBorrowed(Document.@NonNull Id documentId, Patron.@NonNull Id patronId) throws DocumentAlreadyBorrowedException {
        if (this.borrowingRecordRepository.containsCurrentlyBorrowedRecord(documentId, patronId))
            throw new DocumentAlreadyBorrowedException();
    }

    private @NonNull BorrowingRecord createBorrowingRecord(Document.@NonNull Id documentId, Patron.@NonNull Id patronId) {
        val borrowedTimestamp = Instant.now();
        val dueTimestamp = borrowedTimestamp.plus(BORROWING_PERIOD);

        val borrowingRecordId = BorrowingRecord.Id.of(documentId, patronId, borrowedTimestamp);

        return BorrowingRecord.of(borrowingRecordId, dueTimestamp);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String documentISBN;
    }

    public static class DocumentISBNInvalidException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
    public static class DocumentBorrowLimitExceeded extends Exception {}
    public static class DocumentAlreadyBorrowedException extends Exception {}
}
