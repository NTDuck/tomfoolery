package org.tomfoolery.configurations.monolith.terminal.views.action.staff.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.StaffSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.staff.documents.UpdateDocumentContentUseCase;
import org.tomfoolery.core.utils.helpers.adapters.Codec;
import org.tomfoolery.infrastructures.adapters.controllers.staff.documents.UpdateDocumentContentController;

public final class UpdateDocumentContentActionView extends UserActionView {
    private final @NonNull UpdateDocumentContentController controller;

    public static @NonNull UpdateDocumentContentActionView of(@NonNull IOHandler ioHandler, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new UpdateDocumentContentActionView(ioHandler, documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private UpdateDocumentContentActionView(@NonNull IOHandler ioHandler, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioHandler);

        this.controller = UpdateDocumentContentController.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            this.controller.accept(requestObject);
            this.onSuccess();

        } catch (UpdateDocumentContentUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (UpdateDocumentContentUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (UpdateDocumentContentUseCase.DocumentNotFoundException exception) {
            this.onDocumentNotFoundException();
        }
    }

    private UpdateDocumentContentController.@NonNull RequestObject collectRequestObject() {
        val ISBN = this.ioHandler.readLine(Message.Format.PROMPT, "document ISBN");
        val rawDocumentContent = this.ioHandler.readLine(Message.Format.PROMPT, "document content");

        val documentContent = Codec.bytesFromChars(Codec.charsFromCharSequence(rawDocumentContent));

        return UpdateDocumentContentController.RequestObject.of(ISBN, documentContent);
    }

    private void onSuccess() {
        this.nextViewClass = StaffSelectionView.class;

        this.ioHandler.writeLine(Message.Format.SUCCESS, "Document content updated");
    }

    private void onDocumentNotFoundException() {
        this.nextViewClass = StaffSelectionView.class;

        this.ioHandler.writeLine(Message.Format.ERROR, "Document not found");
    }
}
