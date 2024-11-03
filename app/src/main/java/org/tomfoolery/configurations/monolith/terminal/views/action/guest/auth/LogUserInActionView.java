package org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.views.abc.ActionView;
import org.tomfoolery.configurations.monolith.terminal.views.abc.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.StaffSelectionView;
import org.tomfoolery.core.domain.auth.Administrator;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.auth.security.PasswordEncoder;
import org.tomfoolery.core.usecases.guest.auth.LogUserInUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.guest.auth.LogUserInController;
import org.tomfoolery.infrastructures.adapters.presenters.guest.auth.LogUserInPresenter;

import java.util.Map;

public class LogUserInActionView implements ActionView {
    private final @NonNull LogUserInController controller;
    private final @NonNull LogUserInPresenter presenter;

    private @NonNull Class<? extends SelectionView> nextViewClass = GuestSelectionView.class;

    private final @NonNull Map<Class<? extends BaseUser>, Class<? extends SelectionView>> userClassToViewClassMap = Map.of(
        Administrator.class, AdministratorSelectionView.class,
        Patron.class, PatronSelectionView.class,
        Staff.class, StaffSelectionView.class
    );

    private LogUserInActionView(@NonNull UserRepositories userRepositories, @NonNull PasswordEncoder passwordEncoder, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.controller = LogUserInController.of(userRepositories, passwordEncoder, authenticationTokenGenerator, authenticationTokenRepository);
        this.presenter = LogUserInPresenter.of(authenticationTokenGenerator);
    }

    public static @NonNull LogUserInActionView of(@NonNull UserRepositories userRepositories, @NonNull PasswordEncoder passwordEncoder, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new LogUserInActionView(userRepositories, passwordEncoder, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = getRequestObject();

        try {
            val responseModel = this.controller.apply(requestObject);
            val viewModel = this.presenter.apply(responseModel);
            onSuccess(viewModel);
        } catch (LogUserInUseCase.CredentialsInvalidException exception) {
            onCredentialsInvalidException();
        } catch (LogUserInUseCase.UserNotFoundException exception) {
            onUserNotFoundException();
        } catch (LogUserInUseCase.PasswordMismatchException exception) {
            onPasswordMismatchException();
        } catch (LogUserInUseCase.UserAlreadyLoggedInException exception) {
            onUserAlreadyLoggedInException();
        }
    }

    private static LogUserInController.@NonNull RequestObject getRequestObject() {
        val scanner = ScannerManager.getScanner();

        System.out.print("Enter username: ");
        val username = scanner.nextLine();

        System.out.print("Enter password: ");
        val password = scanner.nextLine();

        return LogUserInController.RequestObject.of(username, password);
    }

    private void onSuccess(LogUserInPresenter.@NonNull ViewModel viewModel) {
        val userClass = viewModel.getUserClass();

        this.nextViewClass = userClassToViewClassMap.get(userClass);
        assert nextViewClass != null;

        System.out.println("Success: User logged in.");
    }

    private void onCredentialsInvalidException() {
        this.nextViewClass = GuestSelectionView.class;
        System.out.println("Error: Provided credentials are invalid.");
    }

    private void onUserNotFoundException() {
        this.nextViewClass = GuestSelectionView.class;
        System.out.println("Error: Found no user with matching username.");
    }

    private void onPasswordMismatchException() {
        this.nextViewClass = GuestSelectionView.class;
        System.out.println("Error: Password does not match.");
    }

    private void onUserAlreadyLoggedInException() {
        this.nextViewClass = GuestSelectionView.class;
        System.out.println("Error: User already logged in on another device.");
    }

    @Override
    public @NonNull Class<? extends SelectionView> getNextViewClass() {
        return this.nextViewClass;
    }
}
