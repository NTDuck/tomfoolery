package org.tomfoolery.infrastructures.adapters.controllers.user.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.usecases.user.documents.GetDocumentByIdUseCase;
import org.tomfoolery.infrastructures.utils.contracts.ThrowableFunctionController;

public final class GetDocumentByIdController implements ThrowableFunctionController<GetDocumentByIdController.Request, GetDocumentByIdUseCase.Response> {
    private final @NonNull GetDocumentByIdUseCase useCase;

    public static @NonNull GetDocumentByIdController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        return new GetDocumentByIdController(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    private GetDocumentByIdController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        this.useCase = GetDocumentByIdUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    @Override
    public GetDocumentByIdUseCase.@NonNull Response apply(@NonNull Request requestObject) throws AuthenticatedUserUseCase.AuthenticationTokenNotFoundException, AuthenticatedUserUseCase.AuthenticationTokenInvalidException, GetDocumentByIdUseCase.DocumentNotFoundException {
        val requestModel = generateRequestModelFromRequestObject(requestObject);
        val responseModel = this.useCase.apply(requestModel);

        return responseModel;
    }

    private static GetDocumentByIdUseCase.@NonNull Request generateRequestModelFromRequestObject(@NonNull Request requestObject) {
        val ISBN = requestObject.getISBN();
        val documentId = Document.Id.of(ISBN);

        return GetDocumentByIdUseCase.Request.of(documentId);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String ISBN;
    }
}
