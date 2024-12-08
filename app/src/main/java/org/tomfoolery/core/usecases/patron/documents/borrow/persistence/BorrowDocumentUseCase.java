package org.tomfoolery.core.usecases.patron.documents.borrow.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.relations.BorrowingSession;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.hybrid.documents.HybridDocumentRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

public final class BorrowDocumentUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<BorrowDocumentUseCase.Request> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull BorrowingSessionRepository borrowingSessionRepository;

    private static final @Unsigned int MAX_BORROWED_DOCUMENTS_PER_PATRON = 10;
    public static final @NonNull Duration BORROWING_PERIOD = Duration.ofDays(30);

    public static @NonNull BorrowDocumentUseCase of(@NonNull HybridDocumentRepository documentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new BorrowDocumentUseCase(documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private BorrowDocumentUseCase(@NonNull HybridDocumentRepository documentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
        this.borrowingSessionRepository = borrowingSessionRepository;
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

        val borrowingSessionId = BorrowingSession.Id.of(documentId, patronId);
        this.ensureDocumentIsNotBorrowed(borrowingSessionId);

        val borrowingSession = this.createBorrowingSession(borrowingSessionId);
        this.borrowingSessionRepository.save(borrowingSession);
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
        val numberOfCurrentlyBorrowedDocumentsByPatron = this.borrowingSessionRepository.count(patronId);

        if (numberOfCurrentlyBorrowedDocumentsByPatron >= MAX_BORROWED_DOCUMENTS_PER_PATRON)
            throw new DocumentBorrowLimitExceeded();
    }

    private void ensureDocumentIsNotBorrowed(BorrowingSession.@NonNull Id borrowingSessionId) throws DocumentAlreadyBorrowedException {
        if (this.borrowingSessionRepository.contains(borrowingSessionId))
            throw new DocumentAlreadyBorrowedException();
    }

    private @NonNull BorrowingSession createBorrowingSession(BorrowingSession.@NonNull Id borrowingSessionId) {
        val borrowedTimestamp = Instant.now();
        val dueTimestamp = borrowedTimestamp.plus(BORROWING_PERIOD);

        return BorrowingSession.of(borrowingSessionId, borrowedTimestamp, dueTimestamp);
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
