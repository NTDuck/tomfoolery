package org.tomfoolery.configurations.monolith.terminal.views.action.patron.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.patron.auth.UpdatePatronPasswordUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.auth.UpdatePatronPasswordController;

public final class UpdatePatronPasswordActionView extends UserActionView {
    private final @NonNull UpdatePatronPasswordController controller;

    public static @NonNull UpdatePatronPasswordActionView of(@NonNull IOProvider ioProvider, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new UpdatePatronPasswordActionView(ioProvider, patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private UpdatePatronPasswordActionView(@NonNull IOProvider ioProvider, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(ioProvider);

        this.controller = UpdatePatronPasswordController.of(patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            this.controller.accept(requestObject);
            this.onSuccess();

        } catch (UpdatePatronPasswordUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (UpdatePatronPasswordUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (UpdatePatronPasswordUseCase.PatronNotFoundException exception) {
            this.onPatronNotFoundException();
        } catch (UpdatePatronPasswordUseCase.PasswordInvalidException exception) {
            this.onPasswordInvalidException();
        } catch (UpdatePatronPasswordUseCase.PasswordMismatchException exception) {
            this.onPasswordMismatchException();
        }
    }

    private UpdatePatronPasswordController.@NonNull RequestObject collectRequestObject() {
        val oldPatronPassword = this.ioProvider.readPassword(Message.Format.PROMPT, "current password");
        val newPatronPassword = this.ioProvider.readPassword(Message.Format.PROMPT, "new password");

        return UpdatePatronPasswordController.RequestObject.of(oldPatronPassword, newPatronPassword);
    }

    private void onSuccess() {
        this.nextViewClass = GuestSelectionView.class;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Patron password updated, please log in again");
    }

    private void onPatronNotFoundException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Patron not found");
    }

    private void onPasswordInvalidException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Invalid password");
        this.ioProvider.writeLine("(%s)", Message.PASSWORD_CONSTRAINT);
    }

    private void onPasswordMismatchException() {
        this.nextViewClass = GuestSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Wrong password, so now you are a Guest");
    }
}
