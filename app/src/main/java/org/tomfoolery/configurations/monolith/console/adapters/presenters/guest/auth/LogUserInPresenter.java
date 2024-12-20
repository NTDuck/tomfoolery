package org.tomfoolery.configurations.monolith.console.adapters.presenters.guest.auth;

import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.PatronSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.StaffSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.abc.UserSelectionView;
import org.tomfoolery.infrastructures.adapters.controllers.external.guest.users.authentication.abc.LogUserInController;

import java.util.Map;
import java.util.function.Function;

@NoArgsConstructor(staticName = "of")
public final class LogUserInPresenter implements Function<LogUserInController.ViewModel, LogUserInPresenter.ViewModel> {
    private static final @NonNull Map<LogUserInController.UserType, Class<? extends UserSelectionView>> viewClassesByUserTypes = Map.of(
        LogUserInController.UserType.ADMINISTRATOR, AdministratorSelectionView.class,
        LogUserInController.UserType.PATRON, PatronSelectionView.class,
        LogUserInController.UserType.STAFF, StaffSelectionView.class
    );

    @Override
    public @NonNull ViewModel apply(LogUserInController.ViewModel viewModel) {
        val username = viewModel.getLoggedInUsername();
        val userType = viewModel.getLoggedInUserType();
        val viewClass = viewClassesByUserTypes.get(userType);

        return ViewModel.of(username, viewClass);
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull String loggedInUsername;
        @NonNull Class<? extends UserSelectionView> nextViewClass;
    }
}
