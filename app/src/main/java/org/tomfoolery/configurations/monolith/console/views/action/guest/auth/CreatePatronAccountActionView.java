package org.tomfoolery.configurations.monolith.console.views.action.guest.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.BaseActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.usecases.guest.users.CreatePatronAccountUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.guest.auth.CreatePatronAccountController;

public final class CreatePatronAccountActionView extends BaseActionView {
    private final @NonNull CreatePatronAccountController controller;

    public static @NonNull CreatePatronAccountActionView of(@NonNull IOProvider ioProvider, @NonNull PatronRepository patronRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new CreatePatronAccountActionView(ioProvider, patronRepository, passwordEncoder);
    }

    private CreatePatronAccountActionView(@NonNull IOProvider ioProvider, @NonNull PatronRepository patronRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(ioProvider);

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
        val username = this.ioProvider.readLine(Message.Format.PROMPT, "username");
        val password = this.ioProvider.readPassword(Message.Format.PROMPT, "password");

        val firstName = this.ioProvider.readLine(Message.Format.PROMPT, "first name");
        val lastName = this.ioProvider.readLine(Message.Format.PROMPT, "last name");

        val address = this.ioProvider.readLine(Message.Format.PROMPT, "address");
        val email = this.ioProvider.readLine(Message.Format.PROMPT, "email");

        return CreatePatronAccountController.RequestObject.of(username, password, firstName, lastName, address, email);
    }

    private void onSuccess() {
        this.ioProvider.writeLine(Message.Format.SUCCESS, "Patron account created");
    }

    private void onPatronCredentialsInvalidException() {
        this.ioProvider.writeLine(Message.Format.ERROR, "Invalid username or password");
        this.ioProvider.writeLine("(%s)", Message.USERNAME_CONSTRAINT);
        this.ioProvider.writeLine("(%s)", Message.PASSWORD_CONSTRAINT);
    }

    private void onPatronAlreadyExistsException() {
        this.ioProvider.writeLine(Message.Format.ERROR, "Username already exists");
    }
}
