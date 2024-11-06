package org.tomfoolery.configurations.monolith.terminal.views.action.user.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.terminal.adapters.action.user.SharedActionViewAdapter;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.abc.BaseView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.StaffSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.abc.BaseSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.auth.Administrator;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.domain.auth.abc.BaseUser;

import java.util.Map;

public abstract class SharedActionView extends BaseView {
    private static final @NonNull Map<Class<? extends BaseUser>, Class<? extends BaseSelectionView>> USER_CLASS_TO_VIEW_CLASS_MAP = Map.of(
            Administrator.class, AdministratorSelectionView.class,
            Patron.class, PatronSelectionView.class,
            Staff.class, StaffSelectionView.class
    );

    protected final @NonNull SharedActionViewAdapter adapter;
    protected @NonNull Class<? extends BaseView> nextViewClass = GuestSelectionView.class;

    protected static @NonNull Class<? extends BaseSelectionView> getSelectionViewClassFromUserClass(@NonNull Class<? extends BaseUser> userClass) {
        return USER_CLASS_TO_VIEW_CLASS_MAP.get(userClass);
    }

    protected SharedActionView(@NonNull IOHandler ioHandler, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioHandler);
        this.adapter = SharedActionViewAdapter.of(authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public @NonNull Class<? extends BaseView> getNextViewClass() {
        val viewModel = this.adapter.get();
        val userClass = viewModel.getUserClass();

        return getSelectionViewClassFromUserClass(userClass);
    }
}
