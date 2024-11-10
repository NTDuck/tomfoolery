package org.tomfoolery.infrastructures.adapters.controllers.user.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.user.documents.GetDocumentByIdUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.utils.helpers.dataclasses.ViewableFragmentaryDocument;

public final class GetDocumentByIdController implements ThrowableFunction<GetDocumentByIdController.RequestObject, GetDocumentByIdController.ViewModel> {
    private final @NonNull GetDocumentByIdUseCase getDocumentByIdUseCase;

    public static @NonNull GetDocumentByIdController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        return new GetDocumentByIdController(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    private GetDocumentByIdController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        this.getDocumentByIdUseCase = GetDocumentByIdUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
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
            val fragmentaryDocument = responseModel.getFragmentaryDocument();
            val viewableFragmentaryDocument = ViewableFragmentaryDocument.of(fragmentaryDocument);

            return new ViewModel(viewableFragmentaryDocument);
        }
    }
}