package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepositoryTest;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository", "users", "administrator", "in-memory" })
public class InMemoryAdministratorRepositoryTest extends AdministratorRepositoryTest {
    @Override
    protected @NonNull AdministratorRepository createTestSubject() {
        return InMemoryAdministratorRepository.of();
    }
}