package org.tomfoolery.infrastructures.adapters.presenters.guest.auth;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.usecases.guest.auth.LogUserInUseCase;
import org.tomfoolery.infrastructures.utils.contracts.Presenter;

@RequiredArgsConstructor(staticName = "of")
public final class LogUserInPresenter implements Presenter<LogUserInUseCase.Response, LogUserInPresenter.ViewModel> {
    private final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator;

    @Override
    public @NonNull ViewModel apply(LogUserInUseCase.@NonNull Response responseModel) {
        val authenticationToken = responseModel.getAuthenticationToken();

        val userClass = this.authenticationTokenGenerator.getUserClassFromAuthenticationToken(authenticationToken);
        assert userClass != null;

        return ViewModel.of(userClass);
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull Class<? extends BaseUser> userClass;
    }
}
