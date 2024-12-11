package org.tomfoolery.infrastructures.adapters.controllers.external.staff.documents.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.external.staff.documents.persistence.RemoveDocumentUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

public final class RemoveDocumentController implements ThrowableConsumer<RemoveDocumentController.RequestObject> {
    private final @NonNull RemoveDocumentUseCase removeDocumentUseCase;

    public static @NonNull RemoveDocumentController of(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new RemoveDocumentController(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private RemoveDocumentController(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.removeDocumentUseCase = RemoveDocumentUseCase.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws RemoveDocumentUseCase.AuthenticationTokenNotFoundException, RemoveDocumentUseCase.AuthenticationTokenInvalidException, RemoveDocumentUseCase.DocumentISBNInvalidException, RemoveDocumentUseCase.DocumentNotFoundException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        this.removeDocumentUseCase.accept(requestModel);
    }

    private static RemoveDocumentUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return RemoveDocumentUseCase.Request.of(requestObject.getDocumentISBN());
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String documentISBN;
    }
}
