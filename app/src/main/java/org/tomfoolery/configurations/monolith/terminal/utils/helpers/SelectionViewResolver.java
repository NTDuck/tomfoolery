package org.tomfoolery.configurations.monolith.terminal.utils.helpers;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.terminal.views.selection.abc.BaseSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;

@RequiredArgsConstructor(staticName = "of")
public class SelectionViewResolver {
    private final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator;
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository;

    public @Nullable Class<? extends BaseSelectionView> getMostRecentSelectionView() {
        val authenticationToken = this.authenticationTokenRepository.getAuthenticationToken();

        if (authenticationToken == null)
            return null;

        val userClass = this.authenticationTokenGenerator.getUserClassFromAuthenticationToken(authenticationToken);

        if (userClass == null)
            return null;

        return UserClassAndViewClassRegistry.getViewClassByUserClass(userClass);
    }
}
