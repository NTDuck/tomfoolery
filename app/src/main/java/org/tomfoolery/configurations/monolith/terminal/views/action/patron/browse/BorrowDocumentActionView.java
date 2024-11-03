package org.tomfoolery.configurations.monolith.terminal.views.action.patron.browse;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.views.abc.ActionView;
import org.tomfoolery.configurations.monolith.terminal.views.abc.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.core.dataproviders.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.usecases.patron.documents.BorrowDocumentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.BorrowDocumentController;

public class BorrowDocumentActionView implements ActionView {
    private final @NonNull BorrowDocumentController controller;

    private BorrowDocumentActionView(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.controller = BorrowDocumentController.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    public static @NonNull BorrowDocumentActionView of(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new BorrowDocumentActionView(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = getRequestObject();

        try {
            val responseModel = this.controller.apply(requestObject);
            onSuccess();
        } catch (BorrowDocumentUseCase.PatronAuthenticationTokenNotFoundException exception) {
            onPatronAuthenticationTokenNotFoundException();
        } catch (BorrowDocumentUseCase.PatronAuthenticationTokenInvalidException exception) {
            onPatronAuthenticationTokenInvalidException();
        } catch (BorrowDocumentUseCase.PatronNotFoundException exception) {
            onPatronNotFoundException();
        } catch (BorrowDocumentUseCase.DocumentNotFoundException exception) {
            onDocumentNotFoundException();
        } catch (BorrowDocumentUseCase.DocumentAlreadyBorrowedException exception) {
            onDocumentAlreadyBorrowedException();
        }
    }

    @Override
    public @NonNull Class<? extends SelectionView> getNextViewClass() {
        return PatronSelectionView.class;
    }

    private static BorrowDocumentController.@NonNull RequestObject getRequestObject() {
        val scanner = ScannerManager.getScanner();

        System.out.print("Enter document ID: ");
        val documentIdAsString = scanner.nextLine();

        return BorrowDocumentController.RequestObject.of(documentIdAsString);
    }

    private static void onSuccess() {
        System.out.println("Success: Document borrowed.");
    }

    private static void onPatronAuthenticationTokenNotFoundException() {
        System.out.println("Error: Authentication token not found.");
    }

    private static void onPatronAuthenticationTokenInvalidException() {
        System.out.println("Error: Authentication token is invalid.");
    }

    private static void onPatronNotFoundException() {
        System.out.println("Error: Patron not found.");
    }

    private static void onDocumentNotFoundException() {
        System.out.println("Error: Document not found.");
    }

    private static void onDocumentAlreadyBorrowedException() {
        System.out.println("Error: Document already borrowed.");
    }
}
