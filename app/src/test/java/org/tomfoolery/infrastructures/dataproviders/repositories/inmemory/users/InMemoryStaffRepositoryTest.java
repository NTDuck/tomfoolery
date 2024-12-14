package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepositoryTest;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository", "users", "staff", "in-memory" })
public class InMemoryStaffRepositoryTest extends StaffRepositoryTest {
    @Override
    protected @NonNull StaffRepository createTestSubject() {
        return InMemoryStaffRepository.of();
    }
}