package org.tomfoolery.core.usecases.patron.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
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
import org.tomfoolery.core.utils.dataclasses.auth.security.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.common.Page;

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
        val paginatedFragmentaryBorrowedDocuments = this.getPaginatedFragmentaryBorrowedDocuments(paginatedDocumentIds);

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

    private @NonNull Page<Document.Id> getPaginatedBorrowedDocumentIdsFromPatron(@NonNull Patron patron, @Unsigned int pageIndex, @Unsigned int maxPageSize) throws PaginationInvalidException {
        val borrowedDocumentIds = patron.getAudit().getBorrowedDocumentIds()
            .parallelStream().collect(Collectors.toUnmodifiableList());
        val paginatedBorrowedDocumentIds = Page.fromUnpaginated(borrowedDocumentIds, pageIndex, maxPageSize);

        if (paginatedBorrowedDocumentIds == null)
            throw new PaginationInvalidException();

        return paginatedBorrowedDocumentIds;
    }

    private @NonNull Page<FragmentaryDocument> getPaginatedFragmentaryBorrowedDocuments(@NonNull Page<Document.Id> paginatedBorrowedDocumentIds) {
        return Page.fromPaginated(paginatedBorrowedDocumentIds, this::getFragmentaryDocumentFromId);
    }

    private @NonNull FragmentaryDocument getFragmentaryDocumentFromId(Document.@NonNull Id documentId) {
        val fragmentaryDocument = this.documentRepository.getFragmentById(documentId);
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
        @NonNull Page<FragmentaryDocument> paginatedFragmentaryBorrowedDocuments;
    }

    public static class PatronNotFoundException extends Exception {}
    public static class PaginationInvalidException extends Exception {}
}
