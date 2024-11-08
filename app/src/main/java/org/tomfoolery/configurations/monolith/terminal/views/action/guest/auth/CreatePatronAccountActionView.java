package org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.abc.BaseView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.usecases.guest.auth.CreatePatronAccountUseCase;

public final class CreatePatronAccountActionView extends BaseView {
    private final @NonNull CreatePatronAccountController controller;

    public static @NonNull CreatePatronAccountActionView of(@NonNull IOHandler ioHandler, @NonNull PatronRepository patronRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new CreatePatronAccountActionView(ioHandler, patronRepository, passwordEncoder);
    }

    private CreatePatronAccountActionView(@NonNull IOHandler ioHandler, @NonNull PatronRepository patronRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(ioHandler);

        this.nextViewClass = GuestSelectionView.class;
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
        this.ioHandler.writeLine("(%s)", USERNAME_CONSTRAINT_MESSAGE);
        this.ioHandler.writeLine("(%s)", PASSWORD_CONSTRAINT_MESSAGE);
    }

    private void onPatronAlreadyExistsException() {
        this.ioHandler.writeLine(ERROR_MESSAGE_FORMAT, "Username already exists");
    }
}
