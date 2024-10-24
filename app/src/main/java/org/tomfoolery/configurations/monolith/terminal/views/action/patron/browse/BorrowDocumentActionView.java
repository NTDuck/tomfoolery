package org.tomfoolery.configurations.monolith.terminal.views.action.patron.browse;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.ActionView;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.utils.services.ScannerService;
import org.tomfoolery.configurations.monolith.terminal.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.dataproviders.PatronRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.usecases.external.patron.browse.BorrowDocumentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.browse.BorrowDocumentController;

public class BorrowDocumentActionView implements ActionView {
    private final @NonNull BorrowDocumentController controller;

    private BorrowDocumentActionView(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenService authenticationTokenService, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.controller = BorrowDocumentController.of(documentRepository, patronRepository, authenticationTokenService, authenticationTokenRepository);
    }

    public static @NonNull BorrowDocumentActionView of(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenService authenticationTokenService, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new BorrowDocumentActionView(documentRepository, patronRepository, authenticationTokenService, authenticationTokenRepository);
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
        val scanner = ScannerService.getScanner();

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
