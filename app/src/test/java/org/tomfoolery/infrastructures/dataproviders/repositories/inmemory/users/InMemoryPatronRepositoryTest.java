package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepositoryTest;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository", "users", "patron", "in-memory" })
public class InMemoryPatronRepositoryTest extends PatronRepositoryTest {
    @Override
    protected @NonNull PatronRepository createTestSubject() {
        return InMemoryPatronRepository.of();
    }
}