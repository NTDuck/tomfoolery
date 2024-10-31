package org.tomfoolery.infrastructures.adapters.presenters.guest.auth;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.domain.abc.ReadonlyUser;
import org.tomfoolery.core.usecases.external.guest.auth.LogUserInUseCase;
import org.tomfoolery.infrastructures.utils.contracts.Presenter;

import java.util.Map;

@RequiredArgsConstructor(staticName = "of")
public class LogUserInPresenter implements Presenter<LogUserInUseCase.Response, LogUserInPresenter.ViewModel> {
    private final @NonNull AuthenticationTokenService authenticationTokenService;

    @Override
    public @NonNull ViewModel apply(LogUserInUseCase.@NonNull Response responseModel) {
        val authenticationToken = responseModel.getAuthenticationToken();

        val userClass = this.authenticationTokenService.getUserClassFromToken(authenticationToken);
        assert userClass != null;

        return ViewModel.of(userClass);
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull Class<? extends ReadonlyUser> userClass;
    }
}
