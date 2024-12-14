package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.relations;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepositoryTest;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository", "bi-relation", "in-memory" })
public class InMemoryReviewRepositoryTest extends ReviewRepositoryTest {
    @Override
    protected @NonNull ReviewRepository createTestSubject() {
        return InMemoryReviewRepository.of();
    }
}