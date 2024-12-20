package org.tomfoolery.configurations.monolith.console.views.action.guest.users.authentication;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.adapters.presenters.guest.auth.LogUserInPresenter;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.views.action.abc.BaseActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.utils.containers.users.UserRepositories;
import org.tomfoolery.infrastructures.adapters.controllers.external.guest.users.authentication.LogUserInByAuthenticationTokenController;

public final class LogUserInByAuthenticationTokenActionView extends BaseActionView {
    private final @NonNull LogUserInByAuthenticationTokenController logUserInByAuthenticationTokenController;
    private final @NonNull LogUserInPresenter presenter;

    public static @NonNull LogUserInByAuthenticationTokenActionView of(@NonNull IOProvider ioProvider, @NonNull UserRepositories userRepositories, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new LogUserInByAuthenticationTokenActionView(ioProvider, userRepositories, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private LogUserInByAuthenticationTokenActionView(@NonNull IOProvider ioProvider,@NonNull UserRepositories userRepositories, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.logUserInByAuthenticationTokenController = LogUserInByAuthenticationTokenController.of(userRepositories, authenticationTokenGenerator, authenticationTokenRepository);
        this.presenter = LogUserInPresenter.of();
    }

    @Override
    public void run() {
        try {
            val viewModel = this.logUserInByAuthenticationTokenController.get();
            val adaptedViewModel = this.presenter.apply(viewModel);

            this.ensureUserConsent();
            this.onSuccess(adaptedViewModel);

        } catch (Exception exception) {
            this.onException();
        }
    }

    private void ensureUserConsent() throws UserNotConsentedException {
        val userConsent = this.ioProvider.readLine("Do you want to log in with the previous session (Y/N) ");

        if (!userConsent.equalsIgnoreCase("Y"))
            throw new UserNotConsentedException();
    }

    private void onSuccess(LogUserInPresenter.@NonNull ViewModel viewModel) {
        loggedInUsername = viewModel.getLoggedInUsername();
        this.nextViewClass = viewModel.getNextViewClass();
    }

    private void onException() {
        this.nextViewClass = GuestSelectionView.class;
    }

    private static class UserNotConsentedException extends RuntimeException {}
}
