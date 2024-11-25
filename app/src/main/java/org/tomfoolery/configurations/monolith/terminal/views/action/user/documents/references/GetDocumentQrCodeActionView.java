package org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.references;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.SelectionViewResolver;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.io.TemporaryFileHandler;
import org.tomfoolery.configurations.monolith.terminal.views.action.abc.UserActionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentQrCodeGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.user.documents.references.GetDocumentQrCodeUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.user.documents.GetDocumentQrCodeController;

import java.io.IOException;

public final class GetDocumentQrCodeActionView extends UserActionView {
    private final @NonNull GetDocumentQrCodeController controller;

    private final @NonNull SelectionViewResolver selectionViewResolver;

    public static @NonNull GetDocumentQrCodeActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentQrCodeGenerator documentQrCodeGenerator, @NonNull DocumentUrlGenerator documentUrlGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetDocumentQrCodeActionView(ioProvider, documentRepository, documentQrCodeGenerator, documentUrlGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetDocumentQrCodeActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentQrCodeGenerator documentQrCodeGenerator, @NonNull DocumentUrlGenerator documentUrlGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.controller = GetDocumentQrCodeController.of(documentRepository, documentQrCodeGenerator, documentUrlGenerator, authenticationTokenGenerator, authenticationTokenRepository);

        this.selectionViewResolver = SelectionViewResolver.of(authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            val viewModel = this.controller.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (GetDocumentQrCodeUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (GetDocumentQrCodeUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (GetDocumentQrCodeUseCase.DocumentNotFoundException exception) {
            this.onDocumentNotFoundException();

        } catch (IOException exception) {
            this.onIOException();
        }
    }

    private GetDocumentQrCodeController.@NonNull RequestObject collectRequestObject() {
        val ISBN = this.ioProvider.readLine(Message.Format.PROMPT, "document ISBN");

        return GetDocumentQrCodeController.RequestObject.of(ISBN);
    }

    private void onSuccess(GetDocumentQrCodeController.@NonNull ViewModel viewModel) throws IOException {
        this.nextViewClass = this.selectionViewResolver.getMostRecentSelectionView();

        val documentQrCode = viewModel.getDocumentQrCode();
        TemporaryFileHandler.saveAndOpen(documentQrCode, TemporaryFileHandler.Extension.IMAGE);

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Document QR code should be opened promptly");
    }

    private void onDocumentNotFoundException() {
        this.nextViewClass = this.selectionViewResolver.getMostRecentSelectionView();

        this.ioProvider.writeLine(Message.Format.ERROR, "Document not found");
    }

    private void onIOException() {
        this.nextViewClass = this.selectionViewResolver.getMostRecentSelectionView();

        this.ioProvider.writeLine(Message.Format.ERROR, "Failed to open document QR code");
    }
}
