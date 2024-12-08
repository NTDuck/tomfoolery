package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.documents;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepositoryTest;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository", "documents", "in-memory" })
public class InMemoryDocumentRepositoryTest extends DocumentRepositoryTest {
    @Override
    protected @NonNull DocumentRepository createTestSubject() {
        return InMemoryDocumentRepository.of();
    }
}