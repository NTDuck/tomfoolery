package org.tomfoolery.core.dataproviders.repositories.documents;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepositoryTest;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.utils.helpers.mockers.abc.EntityMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.documents.DocumentMocker;

@Test(groups = { "unit", "repository", "documents" })
public abstract class DocumentRepositoryTest extends BaseRepositoryTest<Document, Document.Id> {
    @Override
    protected abstract @NonNull DocumentRepository createTestSubject();

    @Override
    protected @NonNull EntityMocker<Document, Document.Id> createEntityMocker() {
        return DocumentMocker.of();
    }
}