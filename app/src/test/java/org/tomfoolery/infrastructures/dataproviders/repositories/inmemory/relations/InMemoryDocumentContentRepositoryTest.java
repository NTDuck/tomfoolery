package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.relations;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepositoryTest;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository", "uni-relation", "in-memory" })
public class InMemoryDocumentContentRepositoryTest extends DocumentContentRepositoryTest {
    @Override
    protected @NonNull DocumentContentRepository createTestSubject() {
        return InMemoryDocumentContentRepository.of();
    }
}