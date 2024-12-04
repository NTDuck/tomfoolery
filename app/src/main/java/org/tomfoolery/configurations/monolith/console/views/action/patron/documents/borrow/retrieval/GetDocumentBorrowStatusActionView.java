package org.tomfoolery.configurations.monolith.console.views.action.patron.documents.borrow.retrieval;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.PatronSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.patron.documents.borrow.retrieval.GetDocumentBorrowStatusUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.borrow.retrieval.GetDocumentBorrowStatusController;

public final class GetDocumentBorrowStatusActionView extends UserActionView {
    private final @NonNull GetDocumentBorrowStatusController getDocumentBorrowStatusController;

    public static @NonNull GetDocumentBorrowStatusActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetDocumentBorrowStatusActionView(ioProvider, documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetDocumentBorrowStatusActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.getDocumentBorrowStatusController = GetDocumentBorrowStatusController.of(documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            val viewModel = this.getDocumentBorrowStatusController.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (GetDocumentBorrowStatusUseCase.AuthenticationTokenNotFoundException | GetDocumentBorrowStatusUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (GetDocumentBorrowStatusUseCase.DocumentISBNInvalidException | GetDocumentBorrowStatusUseCase.DocumentNotFoundException | GetDocumentBorrowStatusUseCase.DocumentNotBorrowedException exception) {
            this.onException(exception);
        }
    }

    private GetDocumentBorrowStatusController.@NonNull RequestObject collectRequestObject() {
        val ISBN = this.ioProvider.readLine(Message.Format.PROMPT, "document ISBN");

        return GetDocumentBorrowStatusController.RequestObject.of(ISBN);
    }

    private void displayViewModel(GetDocumentBorrowStatusController.@NonNull ViewModel viewModel) {
        this.ioProvider.writeLine("""
            Document borrow status:
            - Borrowed at: %s
            - Due at: %s""",
            viewModel.getBorrowedTimestamp(),
            viewModel.getDueTimestamp()
        );
    }

    private void onSuccess(GetDocumentBorrowStatusController.@NonNull ViewModel viewModel) {
        this.nextViewClass = PatronSelectionView.class;

        this.displayViewModel(viewModel);
    }
}
