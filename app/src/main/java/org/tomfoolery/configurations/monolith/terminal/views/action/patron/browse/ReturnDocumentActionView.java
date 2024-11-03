package org.tomfoolery.configurations.monolith.terminal.views.action.patron.browse;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.contracts.ActionView;
import org.tomfoolery.configurations.monolith.terminal.utils.contracts.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.ScannerManager;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.core.dataproviders.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.usecases.patron.documents.ReturnDocumentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.ReturnDocumentController;

public class ReturnDocumentActionView implements ActionView {
    private final @NonNull ReturnDocumentController controller;

    private ReturnDocumentActionView(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.controller = ReturnDocumentController.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    public static @NonNull ReturnDocumentActionView of(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ReturnDocumentActionView(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
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
        val scanner = ScannerManager.getScanner();

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
