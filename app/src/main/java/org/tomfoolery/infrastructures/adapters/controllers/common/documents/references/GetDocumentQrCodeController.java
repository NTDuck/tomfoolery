package org.tomfoolery.infrastructures.adapters.controllers.common.documents.references;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentQrCodeGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.common.documents.references.GetDocumentQrCodeUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.TemporaryFileProvider;

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
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws GetDocumentQrCodeUseCase.AuthenticationTokenNotFoundException, GetDocumentQrCodeUseCase.AuthenticationTokenInvalidException, GetDocumentQrCodeUseCase.DocumentISBNInvalidException, GetDocumentQrCodeUseCase.DocumentNotFoundException, DocumentQrCodeUnavailable {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.getDocumentQrCodeUseCase.apply(requestModel);
        val viewModel = mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static GetDocumentQrCodeUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return GetDocumentQrCodeUseCase.Request.of(requestObject.getDocumentISBN());
    }

    private static @NonNull ViewModel mapResponseModelToViewModel(GetDocumentQrCodeUseCase.@NonNull Response responseModel) throws DocumentQrCodeUnavailable {
        val documentQrCode = responseModel.getDocumentQrCode();
        val documentQrCodeFilePath = saveDocumentQrCodeAndGetPath(documentQrCode.getBytes());

        return ViewModel.of(documentQrCodeFilePath);
    }

    private static @NonNull String saveDocumentQrCodeAndGetPath(byte @NonNull [] rawDocumentQrCode) throws DocumentQrCodeUnavailable {
        try {
            return TemporaryFileProvider.save(".png", rawDocumentQrCode);
        } catch (IOException exception) {
            throw new DocumentQrCodeUnavailable();
        }
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String documentISBN;
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull String documentQrCodeFilePath;
    }

    public static class DocumentQrCodeUnavailable extends Exception {}
}
