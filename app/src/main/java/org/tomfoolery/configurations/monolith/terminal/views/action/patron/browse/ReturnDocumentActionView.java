package org.tomfoolery.configurations.monolith.terminal.views.action.patron.browse;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.ActionView;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.utils.services.ScannerService;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.core.dataproviders.DocumentRepository;
import org.tomfoolery.core.dataproviders.PatronRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.usecases.external.patron.browse.ReturnDocumentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.browse.BorrowDocumentController;
import org.tomfoolery.infrastructures.adapters.controllers.patron.browse.ReturnDocumentController;

public class ReturnDocumentActionView implements ActionView {
    private final @NonNull ReturnDocumentController controller;

    private ReturnDocumentActionView(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenService authenticationTokenService, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.controller = ReturnDocumentController.of(documentRepository, patronRepository, authenticationTokenService, authenticationTokenRepository);
    }

    public static @NonNull ReturnDocumentActionView of(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenService authenticationTokenService, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ReturnDocumentActionView(documentRepository, patronRepository, authenticationTokenService, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = getRequestObject();

        try {
            this.controller.accept(requestObject);
        } catch (ReturnDocumentUseCase.PatronAuthenticationTokenNotFoundException e) {
            onPatronAuthenticationTokenNotFoundException();
        } catch (ReturnDocumentUseCase.PatronAuthenticationTokenInvalidException e) {
            onPatronAuthenticationTokenInvalidException();
        } catch (ReturnDocumentUseCase.PatronNotFoundException e) {
            onPatronNotFoundException();
        } catch (ReturnDocumentUseCase.DocumentNotFoundException e) {
            onDocumentNotFoundException();
        } catch (ReturnDocumentUseCase.DocumentNotBorrowedException e) {
            onDocumentNotBorrowedException();
        }
    }

    @Override
    public @NonNull Class<? extends SelectionView> getNextViewClass() {
        return PatronSelectionView.class;
    }

    private static ReturnDocumentController.@NonNull RequestObject getRequestObject() {
        val scanner = ScannerService.getScanner();

        System.out.print("Enter document ID: ");
        val documentIdAsString = scanner.nextLine();

        return ReturnDocumentController.RequestObject.of(documentIdAsString);
    }

    private static void onSuccess() {
        System.out.println("Success: Document returned.");
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

    private static void onDocumentNotBorrowedException() {
        System.out.println("Error: Document not borrowed.");
    }
}
