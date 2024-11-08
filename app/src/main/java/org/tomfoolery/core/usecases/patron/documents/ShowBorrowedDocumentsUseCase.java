package org.tomfoolery.core.usecases.patron.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.Page;

import java.util.Collection;
import java.util.List;

public final class ShowBorrowedDocumentsUseCase extends AuthenticatedUserUseCase implements ThrowableFunction<ShowBorrowedDocumentsUseCase.Request, ShowBorrowedDocumentsUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull PatronRepository patronRepository;

    public static @NonNull ShowBorrowedDocumentsUseCase of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository) {
        return new ShowBorrowedDocumentsUseCase(authenticationTokenGenerator, authenticationTokenRepository, documentRepository, patronRepository);
    }

    private ShowBorrowedDocumentsUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
        this.patronRepository = patronRepository;
    }

    @Override
    protected @NonNull Collection<Class<? extends BaseUser>> getAllowedUserClasses() {
        return List.of(Patron.class);
    }

    @Override
    public @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PatronNotFoundException, PaginationInvalidException {
        val patronAuthenticationToken = getAuthenticationTokenFromRepository();
        ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patron = getPatronFromAuthenticationToken(patronAuthenticationToken);

        int pageIndex = request.getPageIndex();
        int pageSize = request.getPageSize();

        val paginatedDocumentIds = getPaginatedBorrowedDocumentIdsFromPatron(patron, pageIndex, pageSize);
        val paginatedFragmentaryBorrowedDocuments = getPaginatedFragmentaryBorrowedDocuments(paginatedDocumentIds);

        return Response.of(paginatedFragmentaryBorrowedDocuments);
    }

    private @NonNull Patron getPatronFromAuthenticationToken(@NonNull AuthenticationToken staffAuthenticationToken) throws AuthenticationTokenInvalidException, PatronNotFoundException {
        val patronId = this.authenticationTokenGenerator.getUserIdFromAuthenticationToken(staffAuthenticationToken);

        if (patronId == null)
            throw new AuthenticationTokenInvalidException();

        val patron = this.patronRepository.getById(patronId);

        if (patron == null)
            throw new PatronNotFoundException();

        return patron;
    }

    private @NonNull Page<Document.Id> getPaginatedBorrowedDocumentIdsFromPatron(@NonNull Patron patron, int pageIndex, int maxPageSize) throws PaginationInvalidException {
        val borrowedDocumentIds = patron.getAudit().getBorrowedDocumentIds();
        val paginatedBorrowedDocumentIds = Page.fromUnpaginated(borrowedDocumentIds, pageIndex, maxPageSize);

        if (paginatedBorrowedDocumentIds == null)
            throw new PaginationInvalidException();

        return paginatedBorrowedDocumentIds;
    }

    private @NonNull Page<FragmentaryDocument> getPaginatedFragmentaryBorrowedDocuments(@NonNull Page<Document.Id> paginatedBorrowedDocumentIds) {
        return Page.fromPaginated(paginatedBorrowedDocumentIds, this::getFragmentaryDocumentFromId);
    }

    private @NonNull FragmentaryDocument getFragmentaryDocumentFromId(Document.@NonNull Id documentId) {
        val fragmentaryDocument = this.documentRepository.getFragmentaryById(documentId);
        assert fragmentaryDocument != null;
        return fragmentaryDocument;
    }

    @Value(staticConstructor = "of")
    public static class Request {
        int pageIndex;
        int pageSize;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Page<FragmentaryDocument> paginatedFragmentaryBorrowedDocuments;
    }

    public static class PatronNotFoundException extends Exception {}
    public static class PaginationInvalidException extends Exception {}
}
