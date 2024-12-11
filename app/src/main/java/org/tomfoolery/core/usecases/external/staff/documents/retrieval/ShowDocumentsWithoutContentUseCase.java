package org.tomfoolery.core.usecases.external.staff.documents.retrieval;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.DocumentContent;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.usecases.external.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.core.utils.dataclasses.Page;

import java.util.Set;
import java.util.stream.Collectors;

public final class ShowDocumentsWithoutContentUseCase extends AuthenticatedUserUseCase implements ThrowableFunction<ShowDocumentsWithoutContentUseCase.Request, ShowDocumentsWithoutContentUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull DocumentContentRepository documentContentRepository;

    public static @NonNull ShowDocumentsWithoutContentUseCase of(@NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ShowDocumentsWithoutContentUseCase(documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ShowDocumentsWithoutContentUseCase(@NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
        this.documentContentRepository = documentContentRepository;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Staff.class);
    }

    @Override
    public @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PaginationInvalidException {
        val authenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(authenticationToken);

        val pageIndex = request.getPageIndex();
        val maxPageSize = request.getMaxPageSize();

        val paginatedDocumentsWithoutContent = this.getPaginatedDocumentWithoutContent(pageIndex, maxPageSize);

        return Response.of(paginatedDocumentsWithoutContent);
    }

    private @NonNull Page<Document> getPaginatedDocumentWithoutContent(@Unsigned int pageIndex, @Unsigned int maxPageSize) throws PaginationInvalidException {
        val unpaginatedDocuments = this.documentRepository.show().parallelStream()
            .filter(document -> !this.documentContentRepository.contains(DocumentContent.Id.of(document.getId())))
            .collect(Collectors.toUnmodifiableList());

        val paginatedDocuments = Page.fromUnpaginated(unpaginatedDocuments, pageIndex, maxPageSize);

        if (paginatedDocuments == null)
            throw new PaginationInvalidException();

        return paginatedDocuments;
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Page<Document> paginatedDocumentsWithoutContent;
    }

    public static class PaginationInvalidException extends Exception {}
}
