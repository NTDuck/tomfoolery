package org.tomfoolery.core.usecases.documents.retrieval;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.dataclasses.Page;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

public final class ShowDocumentsUseCase extends AuthenticatedUserUseCase implements ThrowableFunction<ShowDocumentsUseCase.Request, ShowDocumentsUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    public static @NonNull ShowDocumentsUseCase of(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ShowDocumentsUseCase(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ShowDocumentsUseCase(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
    }

    @Override
    public @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenInvalidException, AuthenticationTokenNotFoundException, PaginationInvalidException {
        val authenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(authenticationToken);

        val pageIndex = request.getPageIndex();
        val maxPageSize = request.getMaxPageSize();

        val paginatedDocumentsWithoutContent = this.getPaginatedDocumentsWithoutContent(pageIndex, maxPageSize);

        return Response.of(paginatedDocumentsWithoutContent);
    }

    private @NonNull Page<DocumentWithoutContent> getPaginatedDocumentsWithoutContent(@Unsigned int pageIndex, @Unsigned int maxPageSize) throws PaginationInvalidException {
        val paginatedFragmentaryDocuments = this.documentRepository.showPaginatedWithoutContent(pageIndex, maxPageSize);

        if (paginatedFragmentaryDocuments == null)
            throw new PaginationInvalidException();

        return paginatedFragmentaryDocuments;
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

    public static class PaginationInvalidException extends Exception {}
}
