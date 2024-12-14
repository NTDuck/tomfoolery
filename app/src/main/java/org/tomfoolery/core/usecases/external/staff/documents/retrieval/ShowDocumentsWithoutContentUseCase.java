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
        val staffAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(staffAuthenticationToken);

        val pageIndex = request.getPageIndex();
        val maxPageSize = request.getMaxPageSize();

        val documentIdsWithoutContentPage = this.generateDocumentIdsWithoutContentPage(pageIndex, maxPageSize);
        val documentsWithoutContentPage = this.getDocumentsWithoutContentPage(documentIdsWithoutContentPage);

        return Response.of(documentsWithoutContentPage);
    }

    private @NonNull Page<Document.Id> generateDocumentIdsWithoutContentPage(@Unsigned int pageIndex, @Unsigned int maxPageSize) throws PaginationInvalidException {
        val unpaginatedDocumentIds = this.documentRepository.showIds();
        val unpaginatedDocumentIdsWithContent = this.documentContentRepository.showIds().parallelStream()
            .map(DocumentContent.Id::getEntityId)
            .collect(Collectors.toUnmodifiableSet());

        val unpaginatedDocumentIdsWithoutContent = unpaginatedDocumentIds.parallelStream()
            .filter(documentId -> !unpaginatedDocumentIdsWithContent.contains(documentId))
            .collect(Collectors.toUnmodifiableList());

        val documentIdsWithoutContentPage = Page.fromUnpaginated(unpaginatedDocumentIdsWithoutContent, pageIndex, maxPageSize);

        if (documentIdsWithoutContentPage == null)
            throw new PaginationInvalidException();

        return documentIdsWithoutContentPage;
    }

    private @NonNull Page<Document> getDocumentsWithoutContentPage(@NonNull Page<Document.Id> documentIdsWithoutContentPage) {
        return documentIdsWithoutContentPage.map(this.documentRepository::getById);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Page<Document> documentsWithoutContentPage;
    }

    public static class PaginationInvalidException extends Exception {}
}
