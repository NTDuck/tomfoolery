package org.tomfoolery.configurations.monolith.terminal.adapters.controllers.action.user;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.auth.abc.BaseUser;

import java.util.function.Supplier;

@RequiredArgsConstructor(staticName = "of")
public final class SharedActionAdapter implements Supplier<SharedActionAdapter.ViewModel> {
    private final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator;
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository;

    @Override
    public ViewModel get() {
        val authenticationToken = this.authenticationTokenRepository.getAuthenticationToken();
        assert authenticationToken != null;

        val userClass = this.authenticationTokenGenerator.getUserClassFromAuthenticationToken(authenticationToken);
        assert userClass != null;

        return ViewModel.of(userClass);
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull Class<? extends BaseUser> userClass;
    }
}
