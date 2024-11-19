package org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.views.abc.BaseView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.usecases.guest.auth.CreatePatronAccountUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.guest.auth.CreatePatronAccountController;

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
        val requestObject = this.collectRequestObject();

        try {
            this.controller.accept(requestObject);
            this.onSuccess();

        } catch (CreatePatronAccountUseCase.PatronCredentialsInvalidException exception) {
            this.onPatronCredentialsInvalidException();
        } catch (CreatePatronAccountUseCase.PatronAlreadyExistsException exception) {
            this.onPatronAlreadyExistsException();
        }
    }

    private CreatePatronAccountController.@NonNull RequestObject collectRequestObject() {
        val username = this.ioHandler.readLine(Message.Format.PROMPT, "username");
        val password = this.ioHandler.readPassword(Message.Format.PROMPT, "password");

        val firstName = this.ioHandler.readLine(Message.Format.PROMPT, "first name");
        val lastName = this.ioHandler.readLine(Message.Format.PROMPT, "last name");

        val address = this.ioHandler.readLine(Message.Format.PROMPT, "address");
        val email = this.ioHandler.readLine(Message.Format.PROMPT, "email");

        return CreatePatronAccountController.RequestObject.of(username, password, firstName, lastName, address, email);
    }

    private void onSuccess() {
        this.ioHandler.writeLine(Message.Format.SUCCESS, "Patron account created");
    }

    private void onPatronCredentialsInvalidException() {
        this.ioHandler.writeLine(Message.Format.ERROR, "Invalid username or password");
        this.ioHandler.writeLine("(%s)", Message.USERNAME_CONSTRAINT);
        this.ioHandler.writeLine("(%s)", Message.PASSWORD_CONSTRAINT);
    }

    private void onPatronAlreadyExistsException() {
        this.ioHandler.writeLine(Message.Format.ERROR, "Username already exists");
    }
}
