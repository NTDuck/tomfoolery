package org.tomfoolery.infrastructures.adapters.presenters.guest.auth;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.usecases.guest.auth.LogUserInUseCase;
import org.tomfoolery.infrastructures.utils.helpers.reflection.Cloner;

import java.util.function.Function;

@NoArgsConstructor(staticName = "of")
public final class LogUserInPresenter implements Function<LogUserInUseCase.Response, LogUserInPresenter.ViewModel> {
    @Override
    @SneakyThrows
    public @NonNull ViewModel apply(LogUserInUseCase.@NonNull Response responseModel) {
        return Cloner.cloneFrom(responseModel, ViewModel.class);
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull Class<? extends BaseUser> userClass;
    }
}
