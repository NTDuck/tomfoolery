package org.tomfoolery.core.dataproviders.repositories.users;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.users.abc.UserRepositoryTest;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.AdministratorMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.abc.UserMocker;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository", "users", "administrator" })
public abstract class AdministratorRepositoryTest extends UserRepositoryTest<Administrator> {
    @Override
    protected abstract @NonNull AdministratorRepository createTestSubject();

    @Override
    protected @NonNull UserMocker<Administrator> createEntityMocker() {
        return AdministratorMocker.of();
    }
}