package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.relations;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepositoryTest;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository", "bi-relation", "in-memory" })
public class InMemoryBorrowingSessionRepositoryTest extends BorrowingSessionRepositoryTest {
    @Override
    protected @NonNull BorrowingSessionRepository createTestSubject() {
        return InMemoryBorrowingSessionRepository.of();
    }
}