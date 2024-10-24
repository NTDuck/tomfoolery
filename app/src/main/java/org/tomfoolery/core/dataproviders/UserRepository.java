package org.tomfoolery.core.dataproviders;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.abc.BaseRepository;
import org.tomfoolery.core.domain.abc.ReadonlyUser;

public interface UserRepository<User extends ReadonlyUser> extends BaseRepository<User, ReadonlyUser.Id> {
    @Nullable User getByUsername(@NonNull String username);

    @NonNull Class<User> getUserClass();

    default boolean contains(@NonNull String username) {
        return this.getByUsername(username) != null;
    }
}
