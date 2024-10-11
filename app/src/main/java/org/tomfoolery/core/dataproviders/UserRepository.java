package org.tomfoolery.core.dataproviders;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.domain.ReadonlyUser;

public interface UserRepository<User extends ReadonlyUser> extends Repository<User, ReadonlyUser.Id> {
    @Nullable User getByCredentials(User.@NonNull Credentials credentials);

    @NonNull Class<User> getUserClass();
}
