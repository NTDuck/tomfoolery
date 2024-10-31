package org.tomfoolery.core.usecases.external.user.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.external.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.dataclasses.Page;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

public final class ShowDocumentsUseCase extends AuthenticatedUserUseCase implements ThrowableFunction<ShowDocumentsUseCase.Request, ShowDocumentsUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;

    public static @NonNull ShowDocumentsUseCase of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        return new ShowDocumentsUseCase(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    private ShowDocumentsUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);
        this.documentRepository = documentRepository;
    }

    @Override
    public @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenInvalidException, AuthenticationTokenNotFoundException, DocumentsNotFoundException {
        val authenticationToken = getAuthenticationTokenFromRepository();
        ensureAuthenticationTokenIsValid(authenticationToken);

        val pageIndex = request.getPageIndex();
        val pageSize = request.getPageSize();

        val paginatedDocuments = getPaginatedDocuments(pageIndex, pageSize);

        return Response.of(paginatedDocuments);
    }

    private @NonNull Page<Document> getPaginatedDocuments(int pageIndex, int pageSize) throws DocumentsNotFoundException {
        val paginatedDocuments = this.documentRepository.showPaginated(pageIndex, pageSize);

        if (paginatedDocuments == null)
            throw new DocumentsNotFoundException();

        return paginatedDocuments;
    }

    @Value(staticConstructor = "of")
    public static class Request {
        int pageIndex;
        int pageSize;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Page<Document> paginatedDocuments;
    }

    public static class DocumentsNotFoundException extends Exception {}
}
