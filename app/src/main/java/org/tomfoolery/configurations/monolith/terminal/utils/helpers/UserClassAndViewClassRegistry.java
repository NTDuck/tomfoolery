package org.tomfoolery.configurations.monolith.terminal.utils.helpers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.terminal.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.StaffSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.abc.UserSelectionView;
import org.tomfoolery.core.domain.auth.Administrator;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.domain.auth.abc.BaseUser;

import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.NONE)
final class UserClassAndViewClassRegistry {
    private static final @NonNull Map<Class<? extends BaseUser>, Class<? extends UserSelectionView>> viewClassesByUserClasses = Map.of(
        Administrator.class, AdministratorSelectionView.class,
        Patron.class, PatronSelectionView.class,
        Staff.class, StaffSelectionView.class
    );

    private static final @NonNull Map<Class<? extends UserSelectionView>, Class<? extends BaseUser>> userClassesByViewClasses = viewClassesByUserClasses.entrySet().stream()
        .collect(Collectors.toUnmodifiableMap(Map.Entry::getValue, Map.Entry::getKey));

    public static @Nullable Class<? extends BaseUser> getUserClassByViewClass(@NonNull Class<? extends UserSelectionView> viewClass) {
        return userClassesByViewClasses.get(viewClass);
    }

    public static @Nullable Class<? extends UserSelectionView> getViewClassByUserClass(@NonNull Class<? extends BaseUser> userClass) {
        return viewClassesByUserClasses.get(userClass);
    }
}
