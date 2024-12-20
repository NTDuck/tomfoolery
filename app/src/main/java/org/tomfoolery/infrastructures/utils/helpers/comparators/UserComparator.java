package org.tomfoolery.infrastructures.utils.helpers.comparators;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.users.abc.BaseUser;

import java.util.Comparator;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class UserComparator {
    public static <User extends BaseUser> @NonNull Comparator<User.Id> compareId() {
        return Comparator.comparing(User.Id::getUuid);
    }

    public static <User extends BaseUser> @NonNull Comparator<User> byIdAscending() {
        return Comparator.comparing(
            user -> user.getId().getUuid());
    }
}
