package org.tomfoolery.core.usecases.patron.documents.borrow;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.core.utils.dataclasses.auth.security.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.Page;

import java.util.Set;
import java.util.stream.Collectors;

public final class ShowBorrowedDocumentsUseCase extends AuthenticatedUserUseCase implements ThrowableFunction<ShowBorrowedDocumentsUseCase.Request, ShowBorrowedDocumentsUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull PatronRepository patronRepository;

    public static @NonNull ShowBorrowedDocumentsUseCase of(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ShowBorrowedDocumentsUseCase(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ShowBorrowedDocumentsUseCase(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
        this.patronRepository = patronRepository;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Patron.class);
    }

    @Override
    public @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PatronNotFoundException, PaginationInvalidException {
        val patronAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patron = this.getPatronFromAuthenticationToken(patronAuthenticationToken);

        int pageIndex = request.getPageIndex();
        int maxPageSize = request.getMaxPageSize();

        val paginatedDocumentIds = this.getPaginatedBorrowedDocumentIdsFromPatron(patron, pageIndex, maxPageSize);
        val paginatedFragmentaryBorrowedDocuments = this.getPaginatedBorrowedDocumentsWithoutContent(paginatedDocumentIds);

        return Response.of(paginatedFragmentaryBorrowedDocuments);
    }

    private @NonNull Patron getPatronFromAuthenticationToken(@NonNull AuthenticationToken patronAuthenticationToken) throws AuthenticationTokenInvalidException, PatronNotFoundException {
        val patronId = this.getUserIdFromAuthenticationToken(patronAuthenticationToken);
        val patron = this.patronRepository.getById(patronId);

        if (patron == null)
            throw new PatronNotFoundException();

        return patron;
    }

    private @NonNull Page<Document.Id> getPaginatedBorrowedDocumentIdsFromPatron(@NonNull Patron patron, @Unsigned int pageIndex, @Unsigned int maxPageSize) throws PaginationInvalidException {
        val borrowedDocumentIds = patron.getAudit().getBorrowedDocumentIds()
            .parallelStream().collect(Collectors.toUnmodifiableList());
        val paginatedBorrowedDocumentIds = Page.fromUnpaginated(borrowedDocumentIds, pageIndex, maxPageSize);

        if (paginatedBorrowedDocumentIds == null)
            throw new PaginationInvalidException();

        return paginatedBorrowedDocumentIds;
    }

    private @NonNull Page<DocumentWithoutContent> getPaginatedBorrowedDocumentsWithoutContent(@NonNull Page<Document.Id> paginatedBorrowedDocumentIds) {
        return Page.fromPaginated(paginatedBorrowedDocumentIds, this::getFragmentaryDocumentFromId);
    }

    private @NonNull DocumentWithoutContent getFragmentaryDocumentFromId(Document.@NonNull Id documentId) {
        val fragmentaryDocument = this.documentRepository.getByIdWithoutContent(documentId);
        assert fragmentaryDocument != null;

        return fragmentaryDocument;
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Page<DocumentWithoutContent> paginatedDocumentsWithoutContent;
    }

    public static class PatronNotFoundException extends Exception {}
    public static class PaginationInvalidException extends Exception {}
}
