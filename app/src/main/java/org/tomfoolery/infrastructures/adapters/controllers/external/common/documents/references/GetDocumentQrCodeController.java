package org.tomfoolery.infrastructures.adapters.controllers.external.common.documents.references;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentQrCodeGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.external.common.documents.references.GetDocumentQrCodeUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;
import org.tomfoolery.core.dataproviders.repositories.aggregates.hybrids.documents.HybridDocumentRepository;

import java.io.IOException;

public final class GetDocumentQrCodeController implements ThrowableFunction<GetDocumentQrCodeController.RequestObject, GetDocumentQrCodeController.ViewModel> {
    private final @NonNull GetDocumentQrCodeUseCase getDocumentQrCodeUseCase;
    private final @NonNull FileStorageProvider fileStorageProvider;

    public static @NonNull GetDocumentQrCodeController of(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull DocumentQrCodeGenerator documentQrCodeGenerator, @NonNull DocumentUrlGenerator documentUrlGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        return new GetDocumentQrCodeController(hybridDocumentRepository, documentQrCodeGenerator, documentUrlGenerator, authenticationTokenGenerator, authenticationTokenRepository, fileStorageProvider);
    }

    private GetDocumentQrCodeController(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull DocumentQrCodeGenerator documentQrCodeGenerator, @NonNull DocumentUrlGenerator documentUrlGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        this.getDocumentQrCodeUseCase = GetDocumentQrCodeUseCase.of(hybridDocumentRepository, documentQrCodeGenerator, documentUrlGenerator, authenticationTokenGenerator, authenticationTokenRepository);
        this.fileStorageProvider = fileStorageProvider;
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws GetDocumentQrCodeUseCase.AuthenticationTokenNotFoundException, GetDocumentQrCodeUseCase.AuthenticationTokenInvalidException, GetDocumentQrCodeUseCase.DocumentISBNInvalidException, GetDocumentQrCodeUseCase.DocumentNotFoundException, DocumentQrCodeFileWriteException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.getDocumentQrCodeUseCase.apply(requestModel);
        val viewModel = this.mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static GetDocumentQrCodeUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return GetDocumentQrCodeUseCase.Request.of(requestObject.getDocumentISBN());
    }

    private @NonNull ViewModel mapResponseModelToViewModel(GetDocumentQrCodeUseCase.@NonNull Response responseModel) throws DocumentQrCodeFileWriteException {
        val documentQrCode = responseModel.getDocumentQrCode();
        val documentQrCodeFilePath = this.saveDocumentQrCodeAndGetPath(documentQrCode);

        return ViewModel.of(documentQrCodeFilePath);
    }

    private @NonNull String saveDocumentQrCodeAndGetPath(Document.@NonNull QrCode documentQrCode) throws DocumentQrCodeFileWriteException {
        try {
            val rawDocumentQrCode = documentQrCode.getBytes();
            return this.fileStorageProvider.save(rawDocumentQrCode);

        } catch (IOException exception) {
            throw new DocumentQrCodeFileWriteException();
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

    public static class DocumentQrCodeFileWriteException extends Exception {}
}
