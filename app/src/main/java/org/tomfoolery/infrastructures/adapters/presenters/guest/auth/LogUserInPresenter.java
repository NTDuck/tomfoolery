package org.tomfoolery.infrastructures.adapters.presenters.guest.auth;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.StaffSelectionView;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.domain.Administrator;
import org.tomfoolery.core.domain.Patron;
import org.tomfoolery.core.domain.ReadonlyUser;
import org.tomfoolery.core.domain.Staff;
import org.tomfoolery.core.usecases.external.guest.auth.LogUserInUseCase;
import org.tomfoolery.infrastructures.utils.contracts.Presenter;

import java.util.Map;

@RequiredArgsConstructor(staticName = "of")
public class LogUserInPresenter implements Presenter<LogUserInUseCase.Response, LogUserInPresenter.ViewModel> {
    private final @NonNull AuthenticationTokenService authenticationTokenService;

    private final @NonNull Map<Class<? extends ReadonlyUser>, Class<? extends SelectionView>> userClassToViewClassMap = Map.of(
        Administrator.class, AdministratorSelectionView.class,
        Patron.class, PatronSelectionView.class,
        Staff.class, StaffSelectionView.class
    );

    @Override
    public @NonNull ViewModel getViewModelFromResponseModel(LogUserInUseCase.@NonNull Response responseModel) {
        val authenticationToken = responseModel.getAuthenticationToken();

        val userClass = this.authenticationTokenService.getUserClassFromToken(authenticationToken);

        if (userClass == null)
            throw new ViewClassNotFoundException();

        val nextViewClass = userClassToViewClassMap.get(userClass);

        if (nextViewClass == null)
            throw new ViewClassNotFoundException();

        return ViewModel.of(nextViewClass);
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull Class<? extends SelectionView> nextViewClass;
    }

    public static class ViewClassNotFoundException extends RuntimeException {}
}
