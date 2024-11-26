package org.tomfoolery.core.dataproviders.repositories.auth.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.domain.auth.abc.BaseUser;

public interface UserRepository<User extends BaseUser> extends BaseRepository<User, BaseUser.Id> {
    @Nullable User getByUsername(@NonNull String username);

    @NonNull Class<User> getUserClass();

    default boolean contains(@NonNull String username) {
        return this.getByUsername(username) != null;
    }
}
