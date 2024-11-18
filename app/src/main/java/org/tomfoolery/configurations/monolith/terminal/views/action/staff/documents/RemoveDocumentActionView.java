package org.tomfoolery.configurations.monolith.terminal.views.action.staff.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.StaffSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.staff.documents.RemoveDocumentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.staff.documents.RemoveDocumentController;

public final class RemoveDocumentActionView extends UserActionView {
    private final @NonNull RemoveDocumentController controller;

    public static @NonNull RemoveDocumentActionView of(@NonNull IOHandler ioHandler, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new RemoveDocumentActionView(ioHandler, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private RemoveDocumentActionView(@NonNull IOHandler ioHandler, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioHandler);

        this.controller = RemoveDocumentController.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            this.controller.accept(requestObject);
            this.onSuccess();

        } catch (RemoveDocumentUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (RemoveDocumentUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (RemoveDocumentUseCase.DocumentNotFoundException exception) {
            this.onDocumentNotFoundException();
        }
    }

    private RemoveDocumentController.@NonNull RequestObject collectRequestObject() {
        val ISBN = this.ioHandler.readLine(Message.Format.PROMPT, "document ISBN");

        return RemoveDocumentController.RequestObject.of(ISBN);
    }

    private void onSuccess() {
        this.nextViewClass = StaffSelectionView.class;

        this.ioHandler.writeLine(Message.Format.SUCCESS, "Document removed");
    }

    private void onDocumentNotFoundException() {
        this.nextViewClass = StaffSelectionView.class;

        this.ioHandler.writeLine(Message.Format.ERROR, "Document not found");
    }
}
