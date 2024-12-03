package org.tomfoolery.core.usecases.patron.documents.borrow;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.relations.BorrowingSession;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

import java.util.Set;

public final class GetDocumentBorrowStatusUseCase extends AuthenticatedUserUseCase implements ThrowableFunction<GetDocumentBorrowStatusUseCase.Request, GetDocumentBorrowStatusUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull BorrowingSessionRepository borrowingSessionRepository;

    public static @NonNull GetDocumentBorrowStatusUseCase of(@NonNull DocumentRepository documentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetDocumentBorrowStatusUseCase(documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetDocumentBorrowStatusUseCase(@NonNull DocumentRepository documentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
        this.borrowingSessionRepository = borrowingSessionRepository;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Patron.class);
    }

    @Override
    public @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentISBNInvalidException, DocumentNotFoundException {
        val patronAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patronId = this.getUserIdFromAuthenticationToken(patronAuthenticationToken);

        val documentISBN = request.getDocumentISBN();
        val documentId = this.getDocumentIdFromISBN(documentISBN);
        this.ensureDocumentExists(documentId);

        val borowingSessionId = BorrowingSession.Id.of(documentId, patronId);
        val isDocumentBorrowed = this.checkIfDocumentIsBorrowed(borowingSessionId);

        return Response.of(isDocumentBorrowed);
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

    private boolean checkIfDocumentIsBorrowed(BorrowingSession.@NonNull Id borrowingSessionId) {
        return this.borrowingSessionRepository.contains(borrowingSessionId);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String documentISBN;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        boolean isDocumentBorrowed;
    }

    public static class DocumentISBNInvalidException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
}
