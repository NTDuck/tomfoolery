package org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.abc.ActionView;
import org.tomfoolery.configurations.monolith.terminal.views.abc.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.usecases.guest.auth.CreatePatronAccountUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.guest.auth.CreatePatronAccountController;

import java.util.Arrays;

public class CreatePatronAccountActionView implements ActionView {
    private final @NonNull IOHandler ioHandler;

    private final @NonNull CreatePatronAccountController controller;

    public static @NonNull CreatePatronAccountActionView of(@NonNull IOHandler ioHandler, @NonNull PatronRepository patronRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new CreatePatronAccountActionView(ioHandler, patronRepository, passwordEncoder);
    }

    private CreatePatronAccountActionView(@NonNull IOHandler ioHandler, @NonNull PatronRepository patronRepository, @NonNull PasswordEncoder passwordEncoder) {
        this.ioHandler = ioHandler;
        this.controller = CreatePatronAccountController.of(patronRepository, passwordEncoder);
    }

    @Override
    public void run() {
        val requestObject = collectRequestObject();

        try {
            this.controller.accept(requestObject);
            onSuccess();

        } catch (CreatePatronAccountUseCase.PatronCredentialsInvalidException exception) {
            onPatronCredentialsInvalidException();
        } catch (CreatePatronAccountUseCase.PatronAlreadyExistsException exception) {
            onPatronAlreadyExistsException();
        }
    }

    @Override
    public @NonNull Class<? extends SelectionView> getNextViewClass() {
        return GuestSelectionView.class;
    }

    private CreatePatronAccountController.@NonNull Request collectRequestObject() {
        val username = this.ioHandler.readLine(PROMPT_MESSAGE_FORMAT, "username");
        val password = this.ioHandler.readPassword(PROMPT_MESSAGE_FORMAT, "password");

        val fullName = this.ioHandler.readLine(PROMPT_MESSAGE_FORMAT, "full name");
        val address = this.ioHandler.readLine(PROMPT_MESSAGE_FORMAT, "address");
        val email = this.ioHandler.readLine(PROMPT_MESSAGE_FORMAT, "email");

        return CreatePatronAccountController.Request.of(username, new String(password), fullName, address, email);
    }

    private void onSuccess() {
        this.ioHandler.writeLine(SUCCESS_MESSAGE_FORMAT, "Patron account created");
    }

    private void onPatronCredentialsInvalidException() {
        this.ioHandler.writeLine(ERROR_MESSAGE_FORMAT, "Invalid username or password");
        this.ioHandler.writeLine("Username must be 8-16 characters long; contains only lowercase letters, digits, and underscores; must not start with a digit or end with an underscore.");
        this.ioHandler.writeLine("Password must be 8-32 characters long; contains only letters, digits, and underscores.");
    }

    private void onPatronAlreadyExistsException() {
        this.ioHandler.writeLine(ERROR_MESSAGE_FORMAT, "Username already exists");
    }
}
