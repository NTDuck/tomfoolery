package org.tomfoolery.core.usecases.patron.documents.borrow;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingRecordRepository;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.relations.BorrowingRecord;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.core.utils.dataclasses.Page;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ShowBorrowedDocumentsUseCase extends AuthenticatedUserUseCase implements ThrowableFunction<ShowBorrowedDocumentsUseCase.Request, ShowBorrowedDocumentsUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull BorrowingRecordRepository borrowingRecordRepository;

    public static @NonNull ShowBorrowedDocumentsUseCase of(@NonNull DocumentRepository documentRepository, @NonNull BorrowingRecordRepository borrowingRecordRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ShowBorrowedDocumentsUseCase(documentRepository, borrowingRecordRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ShowBorrowedDocumentsUseCase(@NonNull DocumentRepository documentRepository, @NonNull BorrowingRecordRepository borrowingRecordRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
        this.borrowingRecordRepository = borrowingRecordRepository;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Patron.class);
    }

    @Override
    public @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PaginationInvalidException {
        val patronAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patronId = this.getUserIdFromAuthenticationToken(patronAuthenticationToken);

        val pageIndex = request.getPageIndex();
        val maxPageSize = request.getMaxPageSize();

        val currentlyBorrowedRecords = this.getCurrentlyBorrowedRecordsByPatron(patronId);
        val paginatedBorrowedDocumentIds = this.getPaginatedBorrowedDocumentIds(currentlyBorrowedRecords, pageIndex, maxPageSize);
        val paginatedBorrowedDocuments = this.getPaginatedBorrowedDocuments(paginatedBorrowedDocumentIds);

        return Response.of(paginatedBorrowedDocuments);
    }

    private @NonNull List<BorrowingRecord> getCurrentlyBorrowedRecordsByPatron(Patron.@NonNull Id patronId) {
        return this.borrowingRecordRepository.showCurrentlyBorrowedRecordsByPatron(patronId);
    }

    private @NonNull Page<Document.Id> getPaginatedBorrowedDocumentIds(@NonNull List<BorrowingRecord> borrowingRecords, @Unsigned int pageIndex, @Unsigned int maxPageSize) throws PaginationInvalidException {
        val unpaginatedBorrowedDocumentIds = borrowingRecords.parallelStream()
            .map(borrowingRecord -> borrowingRecord.getId().getFirstEntityId())
            .collect(Collectors.toUnmodifiableList());
        val paginatedBorrowedDocumentIds = Page.fromUnpaginated(unpaginatedBorrowedDocumentIds, pageIndex, maxPageSize);

        if (paginatedBorrowedDocumentIds == null)
            throw new PaginationInvalidException();

        return paginatedBorrowedDocumentIds;
    }

    private @NonNull Page<Document> getPaginatedBorrowedDocuments(@NonNull Page<Document.Id> paginatedBorrowedDocumentIds) {
        return Page.fromPaginated(paginatedBorrowedDocumentIds, this::getDocumentById);
    }

    private @NonNull Document getDocumentById(Document.@NonNull Id documentId) {
        val document = this.documentRepository.getById(documentId);
        assert document != null;

        return document;
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Page<Document> paginatedBorrowedDocuments;
    }

    public static class PaginationInvalidException extends Exception {}
}
