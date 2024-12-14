package org.tomfoolery.configurations.monolith.console.views.action.staff.documents.persistence;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.StaffSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.staff.documents.persistence.UpdateDocumentCoverImageUseCase;
import org.tomfoolery.core.dataproviders.providers.io.file.FileVerifier;
import org.tomfoolery.infrastructures.adapters.controllers.external.staff.documents.persistence.UpdateDocumentCoverImageController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;

public final class UpdateDocumentCoverImageActionView extends UserActionView {
    private final @NonNull UpdateDocumentCoverImageController updateDocumentCoverImageController;

    public static @NonNull UpdateDocumentCoverImageActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileVerifier fileVerifier, @NonNull FileStorageProvider fileStorageProvider) {
        return new UpdateDocumentCoverImageActionView(ioProvider, documentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileVerifier, fileStorageProvider);
    }

    private UpdateDocumentCoverImageActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileVerifier fileVerifier, @NonNull FileStorageProvider fileStorageProvider) {
        super(ioProvider);

        this.updateDocumentCoverImageController = UpdateDocumentCoverImageController.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileVerifier, fileStorageProvider);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            this.updateDocumentCoverImageController.accept(requestObject);
            this.onSuccess();

        } catch (UpdateDocumentCoverImageUseCase.AuthenticationTokenNotFoundException | UpdateDocumentCoverImageUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (UpdateDocumentCoverImageUseCase.DocumentISBNInvalidException | UpdateDocumentCoverImageUseCase.DocumentNotFoundException | UpdateDocumentCoverImageUseCase.DocumentCoverImageInvalidException | UpdateDocumentCoverImageController.DocumentCoverImageFilePathInvalidException exception) {
            this.onException(exception);
        }
    }

    private UpdateDocumentCoverImageController.@NonNull RequestObject collectRequestObject() {
        val ISBN = this.ioProvider.readLine(Message.Format.PROMPT, "document ISBN");
        val documentCoverImageFilePath = this.ioProvider.readLine(Message.Format.PROMPT, "document cover image file path");

        return UpdateDocumentCoverImageController.RequestObject.of(ISBN, documentCoverImageFilePath);
    }

    private void onSuccess() {
        this.nextViewClass = StaffSelectionView.class;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Document cover image updated");
    }
}
