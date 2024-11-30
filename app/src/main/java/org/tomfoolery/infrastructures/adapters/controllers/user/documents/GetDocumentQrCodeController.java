package org.tomfoolery.infrastructures.adapters.controllers.user.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentQrCodeGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.documents.retrieval.references.GetDocumentQrCodeUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.utils.helpers.io.file.FileManager;

import java.io.IOException;

public final class GetDocumentQrCodeController implements ThrowableFunction<GetDocumentQrCodeController.RequestObject, GetDocumentQrCodeController.ViewModel> {
    private final @NonNull GetDocumentQrCodeUseCase getDocumentQrCodeUseCase;

    public static @NonNull GetDocumentQrCodeController of(@NonNull DocumentRepository documentRepository, @NonNull DocumentQrCodeGenerator documentQrCodeGenerator, @NonNull DocumentUrlGenerator documentUrlGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetDocumentQrCodeController(documentRepository, documentQrCodeGenerator, documentUrlGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetDocumentQrCodeController(@NonNull DocumentRepository documentRepository, @NonNull DocumentQrCodeGenerator documentQrCodeGenerator, @NonNull DocumentUrlGenerator documentUrlGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.getDocumentQrCodeUseCase = GetDocumentQrCodeUseCase.of(documentRepository, documentQrCodeGenerator, documentUrlGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws GetDocumentQrCodeUseCase.AuthenticationTokenNotFoundException, GetDocumentQrCodeUseCase.AuthenticationTokenInvalidException, GetDocumentQrCodeUseCase.DocumentNotFoundException, DocumentQrCodeUnavailable {
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
        @NonNull String documentQrCodeFilePath;

        private static @NonNull ViewModel fromResponseModel(GetDocumentQrCodeUseCase.@NonNull Response responseModel) throws DocumentQrCodeUnavailable {
            val documentQrCode = responseModel.getDocumentQrCode();
            val rawDocumentQrCode = documentQrCode.getBuffer();

            val documentQrCodeFilePath = saveDocumentQrCodeAndGetPath(rawDocumentQrCode);

            return new ViewModel(documentQrCodeFilePath);
        }

        private static @NonNull String saveDocumentQrCodeAndGetPath(byte @NonNull [] rawDocumentQrCode) throws DocumentQrCodeUnavailable {
            try {
                return FileManager.save(".png", rawDocumentQrCode);
            } catch (IOException exception) {
                throw new DocumentQrCodeUnavailable();
            }
        }
    }

    public static class DocumentQrCodeUnavailable extends Exception {}
}
