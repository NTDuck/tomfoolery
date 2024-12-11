package org.tomfoolery.configurations.monolith.console.views.action.common.documents.references;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentQrCodeGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.common.documents.references.GetDocumentQrCodeUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.common.documents.references.GetDocumentQrCodeController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.hybrid.documents.HybridDocumentRepository;

import java.io.IOException;

public final class GetDocumentQrCodeActionView extends UserActionView {
    private final @NonNull GetDocumentQrCodeController getDocumentQrCodeController;
    private final @NonNull FileStorageProvider fileStorageProvider;

    public static @NonNull GetDocumentQrCodeActionView of(@NonNull IOProvider ioProvider, @NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull DocumentQrCodeGenerator documentQrCodeGenerator, @NonNull DocumentUrlGenerator documentUrlGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        return new GetDocumentQrCodeActionView(ioProvider, hybridDocumentRepository, documentQrCodeGenerator, documentUrlGenerator, authenticationTokenGenerator, authenticationTokenRepository, fileStorageProvider);
    }

    private GetDocumentQrCodeActionView(@NonNull IOProvider ioProvider, @NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull DocumentQrCodeGenerator documentQrCodeGenerator, @NonNull DocumentUrlGenerator documentUrlGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        super(ioProvider);

        this.getDocumentQrCodeController = GetDocumentQrCodeController.of(hybridDocumentRepository, documentQrCodeGenerator, documentUrlGenerator, authenticationTokenGenerator, authenticationTokenRepository, fileStorageProvider);
        this.fileStorageProvider = fileStorageProvider;
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            val viewModel = this.getDocumentQrCodeController.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (GetDocumentQrCodeUseCase.AuthenticationTokenNotFoundException | GetDocumentQrCodeUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (GetDocumentQrCodeUseCase.DocumentISBNInvalidException | GetDocumentQrCodeUseCase.DocumentNotFoundException | GetDocumentQrCodeController.DocumentQrCodeFileWriteException | DocumentQrCodeFileReadException exception) {
            this.onException(exception);
        }
    }

    private GetDocumentQrCodeController.@NonNull RequestObject collectRequestObject() {
        val ISBN = this.ioProvider.readLine(Message.Format.PROMPT, "document ISBN");

        return GetDocumentQrCodeController.RequestObject.of(ISBN);
    }

    private void displayViewModel(GetDocumentQrCodeController.@NonNull ViewModel viewModel) throws DocumentQrCodeFileReadException {
        val documentQrCodeFilePath = viewModel.getDocumentQrCodeFilePath();

        try {
            fileStorageProvider.open(documentQrCodeFilePath);
        } catch (IOException exception) {
            throw new DocumentQrCodeFileReadException();
        }

        this.ioProvider.writeLine(Message.Format.SUCCESS, "The QR code should be opened promptly");
    }

    private void onSuccess(GetDocumentQrCodeController.@NonNull ViewModel viewModel) throws DocumentQrCodeFileReadException {
        this.nextViewClass = cachedViewClass;

        this.displayViewModel(viewModel);
    }

    private static class DocumentQrCodeFileReadException extends Exception {}
}
