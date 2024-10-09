package org.tomfoolery.core.dataproviders;

import org.tomfoolery.core.domain.ReadonlyUser;

import javax.annotation.Nullable;

public interface UserRepository<User extends ReadonlyUser> extends Repository<User, ReadonlyUser.ID> {
    @Nullable User getByCredentials(User.Credentials credentials);
}
