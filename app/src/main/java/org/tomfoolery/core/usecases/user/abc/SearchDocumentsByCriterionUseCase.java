package org.tomfoolery.core.usecases.user.abc;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.core.utils.dataclasses.Page;

public abstract class SearchDocumentsByCriterionUseCase extends AuthenticatedUserUseCase implements ThrowableFunction<SearchDocumentsByCriterionUseCase.Request, SearchDocumentsByCriterionUseCase.Response> {
    protected final @NonNull DocumentRepository documentRepository;

    protected SearchDocumentsByCriterionUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);
        this.documentRepository = documentRepository;
    }

    protected abstract @Nullable Page<Document> getPaginatedDocumentsFromCriterion(@NonNull String criterion, int pageIndex, int pageSize);

    @Override
    public final @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentsNotFoundException {
        val userAuthenticationToken = getAuthenticationTokenFromRepository();
        ensureAuthenticationTokenIsValid(userAuthenticationToken);

        val criterion = request.getCriterion();
        val pageIndex = request.getPageIndex();
        val pageSize = request.getPageSize();

        val paginatedDocuments = getPaginatedDocuments(criterion, pageIndex, pageSize);

        return Response.of(paginatedDocuments);
    }

    private @NonNull Page<Document> getPaginatedDocuments(@NonNull String criterion, int pageIndex, int pageSize) throws DocumentsNotFoundException {
        val paginatedDocuments = getPaginatedDocumentsFromCriterion(criterion, pageIndex, pageSize);

        if (paginatedDocuments == null)
            throw new DocumentsNotFoundException();

        return paginatedDocuments;
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String criterion;
        int pageIndex;
        int pageSize;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Page<Document> paginatedDocuments;
    }

    public static class DocumentsNotFoundException extends Exception {}
}
