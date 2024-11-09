package org.tomfoolery.infrastructures.adapters.controllers.user.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentQrCodeGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.user.documents.references.GetDocumentQrCodeUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

public final class GetDocumentQrCodeController implements ThrowableFunction<GetDocumentQrCodeController.RequestObject, GetDocumentQrCodeController.ViewModel> {
    private final @NonNull GetDocumentQrCodeUseCase getDocumentQrCodeUseCase;

    public static @NonNull GetDocumentQrCodeController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull DocumentQrCodeGenerator documentQrCodeGenerator, @NonNull DocumentUrlGenerator documentUrlGenerator) {
        return new GetDocumentQrCodeController(authenticationTokenGenerator, authenticationTokenRepository, documentRepository, documentQrCodeGenerator, documentUrlGenerator);
    }

    private GetDocumentQrCodeController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull DocumentQrCodeGenerator documentQrCodeGenerator, @NonNull DocumentUrlGenerator documentUrlGenerator) {
        this.getDocumentQrCodeUseCase = GetDocumentQrCodeUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository, documentQrCodeGenerator, documentUrlGenerator);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws GetDocumentQrCodeUseCase.AuthenticationTokenNotFoundException, GetDocumentQrCodeUseCase.AuthenticationTokenInvalidException, GetDocumentQrCodeUseCase.DocumentNotFoundException {
        val requestModel = requestObject.toRequestModel();
        val responseModel = this.getDocumentQrCodeUseCase.apply(requestModel);
        val viewModel = ViewModel.fromResponseModel(responseModel);

        return viewModel;
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String ISBN;

        private GetDocumentQrCodeUseCase.@NonNull Request toRequestModel() {
            val documentId = Document.Id.of(ISBN);

            return GetDocumentQrCodeUseCase.Request.of(documentId);
        }
    }

    @Value
    public static class ViewModel {
        byte @NonNull [] documentQrCode;

        private static @NonNull ViewModel fromResponseModel(GetDocumentQrCodeUseCase.@NonNull Response responseModel) {
            val documentQrCode = responseModel.getDocumentQrCode();
            val rawDocumentQrCode = documentQrCode.getBytes();

            return new ViewModel(rawDocumentQrCode);
        }
    }
}
