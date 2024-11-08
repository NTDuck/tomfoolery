package org.tomfoolery.infrastructures.adapters.presenters.guest.auth;

import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.usecases.guest.auth.LogUserInUseCase;

import java.util.function.Function;

@NoArgsConstructor(staticName = "of")
public final class LogUserInPresenter implements Function<LogUserInUseCase.Response, LogUserInPresenter.ViewModel> {
    @Override
    public @NonNull ViewModel apply(LogUserInUseCase.@NonNull Response responseModel) {
        val userClass = responseModel.getUserClass();
        return ViewModel.of(userClass);
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull Class<? extends BaseUser> userClass;
    }
}
