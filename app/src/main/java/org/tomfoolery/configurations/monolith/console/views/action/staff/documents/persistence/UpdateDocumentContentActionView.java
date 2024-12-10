package org.tomfoolery.configurations.monolith.console.views.action.staff.documents.persistence;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.StaffSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.usecases.staff.documents.persistence.UpdateDocumentContentUseCase;
import org.tomfoolery.core.utils.helpers.verifiers.FileVerifier;
import org.tomfoolery.infrastructures.adapters.controllers.staff.documents.persistence.UpdateDocumentContentController;

public final class UpdateDocumentContentActionView extends UserActionView {
    private final @NonNull UpdateDocumentContentController controller;

    public static @NonNull UpdateDocumentContentActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileVerifier fileVerifier) {
        return new UpdateDocumentContentActionView(ioProvider, documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileVerifier);
    }

    private UpdateDocumentContentActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileVerifier fileVerifier) {
        super(ioProvider);

        this.controller = UpdateDocumentContentController.of(documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileVerifier);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            this.controller.accept(requestObject);
            this.onSuccess();

        } catch (UpdateDocumentContentUseCase.AuthenticationTokenNotFoundException | AuthenticatedUserUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (UpdateDocumentContentController.DocumentContentFilePathInvalidException | UpdateDocumentContentUseCase.DocumentISBNInvalidException | UpdateDocumentContentUseCase.DocumentNotFoundException | UpdateDocumentContentUseCase.DocumentContentInvalidException exception) {
            this.onException(exception);
        }
    }

    private UpdateDocumentContentController.@NonNull RequestObject collectRequestObject() {
        val ISBN = this.ioProvider.readLine(Message.Format.PROMPT, "document ISBN");
        val documentContentFilePath = this.ioProvider.readLine(Message.Format.PROMPT, "document file path");

        return UpdateDocumentContentController.RequestObject.of(ISBN, documentContentFilePath);
    }

    private void onSuccess() {
        this.nextViewClass = StaffSelectionView.class;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Document content updated");
    }
}