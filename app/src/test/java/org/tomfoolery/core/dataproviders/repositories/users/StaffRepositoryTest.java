package org.tomfoolery.core.dataproviders.repositories.users;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.users.abc.UserRepositoryTest;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.StaffMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.abc.UserMocker;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository", "users", "staff" })
public abstract class StaffRepositoryTest extends UserRepositoryTest<Staff> {
    @Override
    protected abstract @NonNull StaffRepository createTestSubject();

    @Override
    protected @NonNull UserMocker<Staff> createEntityMocker() {
        return StaffMocker.of();
    }
}