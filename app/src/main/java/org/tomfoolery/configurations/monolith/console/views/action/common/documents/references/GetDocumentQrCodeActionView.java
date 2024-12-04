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
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.common.documents.references.GetDocumentQrCodeUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.common.documents.references.GetDocumentQrCodeController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.TemporaryFileProvider;

import java.io.IOException;

public final class GetDocumentQrCodeActionView extends UserActionView {
    private final @NonNull GetDocumentQrCodeController getDocumentQrCodeController;

    public static @NonNull GetDocumentQrCodeActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentQrCodeGenerator documentQrCodeGenerator, @NonNull DocumentUrlGenerator documentUrlGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetDocumentQrCodeActionView(ioProvider, documentRepository, documentQrCodeGenerator, documentUrlGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetDocumentQrCodeActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentQrCodeGenerator documentQrCodeGenerator, @NonNull DocumentUrlGenerator documentUrlGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.getDocumentQrCodeController = GetDocumentQrCodeController.of(documentRepository, documentQrCodeGenerator, documentUrlGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            val viewModel = this.getDocumentQrCodeController.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (GetDocumentQrCodeUseCase.AuthenticationTokenNotFoundException | GetDocumentQrCodeUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (GetDocumentQrCodeUseCase.DocumentISBNInvalidException | GetDocumentQrCodeUseCase.DocumentNotFoundException | GetDocumentQrCodeController.DocumentQrCodeUnavailable | DocumentQrCodeNotOpenableException exception) {
            this.onException(exception);
        }
    }

    private GetDocumentQrCodeController.@NonNull RequestObject collectRequestObject() {
        val ISBN = this.ioProvider.readLine(Message.Format.PROMPT, "document ISBN");

        return GetDocumentQrCodeController.RequestObject.of(ISBN);
    }

    private void displayViewModel(GetDocumentQrCodeController.@NonNull ViewModel viewModel) throws DocumentQrCodeNotOpenableException {
        val documentQrCodeFilePath = viewModel.getDocumentQrCodeFilePath();

        try {
            TemporaryFileProvider.open(documentQrCodeFilePath);
        } catch (IOException exception) {
            throw new DocumentQrCodeNotOpenableException();
        }

        this.ioProvider.writeLine(Message.Format.SUCCESS, "The QR code should be opened promptly");
    }

    private void onSuccess(GetDocumentQrCodeController.@NonNull ViewModel viewModel) throws DocumentQrCodeNotOpenableException {
        this.nextViewClass = cachedViewClass;

        this.displayViewModel(viewModel);
    }

        private static class DocumentQrCodeNotOpenableException extends Exception {}
}
