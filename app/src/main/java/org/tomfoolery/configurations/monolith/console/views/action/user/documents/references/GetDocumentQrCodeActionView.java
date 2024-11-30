package org.tomfoolery.configurations.monolith.console.views.action.user.documents.references;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentQrCodeGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.documents.retrieval.references.GetDocumentQrCodeUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.user.documents.GetDocumentQrCodeController;
import org.tomfoolery.infrastructures.utils.helpers.io.file.FileManager;

import java.io.IOException;

public final class GetDocumentQrCodeActionView extends UserActionView {
    private final @NonNull GetDocumentQrCodeController controller;

    public static @NonNull GetDocumentQrCodeActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentQrCodeGenerator documentQrCodeGenerator, @NonNull DocumentUrlGenerator documentUrlGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetDocumentQrCodeActionView(ioProvider, documentRepository, documentQrCodeGenerator, documentUrlGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetDocumentQrCodeActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentQrCodeGenerator documentQrCodeGenerator, @NonNull DocumentUrlGenerator documentUrlGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.controller = GetDocumentQrCodeController.of(documentRepository, documentQrCodeGenerator, documentUrlGenerator, authenticationTokenGenerator, authenticationTokenRepository);
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

        } catch (GetDocumentQrCodeController.DocumentQrCodeUnavailable | IOException exception) {
            this.onDocumentQrCodeUnavailable();
        }
    }

    private GetDocumentQrCodeController.@NonNull RequestObject collectRequestObject() {
        val ISBN = this.ioProvider.readLine(Message.Format.PROMPT, "document ISBN");

        return GetDocumentQrCodeController.RequestObject.of(ISBN);
    }

    private void displayViewModel(GetDocumentQrCodeController.@NonNull ViewModel viewModel) throws IOException {
        val documentQrCodeFilePath = viewModel.getDocumentQrCodeFilePath();
        FileManager.open(documentQrCodeFilePath);

        this.ioProvider.writeLine(Message.Format.SUCCESS, "The QR code should be opened promptly");
    }

    private void onSuccess(GetDocumentQrCodeController.@NonNull ViewModel viewModel) throws IOException {
        this.nextViewClass = cachedViewClass;
        this.displayViewModel(viewModel);
    }

    private void onDocumentNotFoundException() {
        this.nextViewClass = cachedViewClass;

        this.ioProvider.writeLine(Message.Format.ERROR, "Document not found");
    }

    private void onDocumentQrCodeUnavailable() {
        this.nextViewClass = cachedViewClass;

        this.ioProvider.writeLine(Message.Format.ERROR, "Failed to open QR code");
    }
}
