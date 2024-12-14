package org.tomfoolery.core.dataproviders.repositories.users;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.users.abc.UserRepositoryTest;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.PatronMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.abc.UserMocker;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository", "users", "patron" })
public abstract class PatronRepositoryTest extends UserRepositoryTest<Patron> {
    @Override
    protected abstract @NonNull PatronRepository createTestSubject();

    @Override
    protected @NonNull UserMocker<Patron> createEntityMocker() {
        return PatronMocker.of();
    }
}