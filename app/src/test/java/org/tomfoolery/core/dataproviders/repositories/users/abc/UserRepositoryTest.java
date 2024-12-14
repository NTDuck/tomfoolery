package org.tomfoolery.core.dataproviders.repositories.users.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepositoryTest;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.abc.UserMocker;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository", "users" })
public abstract class UserRepositoryTest<User extends BaseUser> extends BaseRepositoryTest<User, BaseUser.Id> {
    @Override
    protected abstract @NonNull UserRepository<User> createTestSubject();

    @Override
    protected abstract @NonNull UserMocker<User> createEntityMocker();
}