package org.tomfoolery.core.usecases.external.staff.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.external.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

import java.util.Collection;
import java.util.List;

public final class RemoveDocumentUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<RemoveDocumentUseCase.Request> {
    private final @NonNull DocumentRepository documentRepository;

    public static @NonNull RemoveDocumentUseCase of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        return new RemoveDocumentUseCase(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    private RemoveDocumentUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);
        this.documentRepository = documentRepository;
    }

    @Override
    protected @NonNull Collection<Class<? extends BaseUser>> getAllowedUserClasses() {
        return List.of(Staff.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentNotFoundException {
        val staffAuthenticationToken = getAuthenticationTokenFromRepository();
        ensureAuthenticationTokenIsValid(staffAuthenticationToken);

        val documentId = request.getDocumentId();
        ensureDocumentExists(documentId);

        this.documentRepository.delete(documentId);
    }

    private void ensureDocumentExists(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        if (!this.documentRepository.contains(documentId))
            throw new DocumentNotFoundException();
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Document.@NonNull Id documentId;
    }

    public static class DocumentNotFoundException extends Exception {}
}
