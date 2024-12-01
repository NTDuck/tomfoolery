package org.tomfoolery.infrastructures.adapters.controllers.user.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.shared.documents.GetDocumentByIdUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.utils.dataclasses.ViewableFragmentaryDocument;

public final class GetDocumentByIdController implements ThrowableFunction<GetDocumentByIdController.RequestObject, GetDocumentByIdController.ViewModel> {
    private final @NonNull GetDocumentByIdUseCase getDocumentByIdUseCase;

    public static @NonNull GetDocumentByIdController of(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetDocumentByIdController(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetDocumentByIdController(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.getDocumentByIdUseCase = GetDocumentByIdUseCase.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws GetDocumentByIdUseCase.AuthenticationTokenNotFoundException, GetDocumentByIdUseCase.AuthenticationTokenInvalidException, GetDocumentByIdUseCase.DocumentNotFoundException {
        val requestModel = requestObject.toRequestModel();
        val responseModel = this.getDocumentByIdUseCase.apply(requestModel);
        val viewModel = ViewModel.fromResponseModel(responseModel);

        return viewModel;
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String ISBN;

        private GetDocumentByIdUseCase.@NonNull Request toRequestModel() {
            val documentId = Document.Id.of(ISBN);

            return GetDocumentByIdUseCase.Request.of(documentId);
        }
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull ViewableFragmentaryDocument fragmentaryDocument;

        private static @NonNull ViewModel fromResponseModel(GetDocumentByIdUseCase.@NonNull Response responseModel) {
            val fragmentaryDocument = responseModel.getDocumentWithoutContent();
            val viewableFragmentaryDocument = ViewableFragmentaryDocument.of(fragmentaryDocument);

            return new ViewModel(viewableFragmentaryDocument);
        }
    }
}
