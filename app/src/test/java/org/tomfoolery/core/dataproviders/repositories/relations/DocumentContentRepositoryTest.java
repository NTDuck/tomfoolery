package org.tomfoolery.core.dataproviders.repositories.relations;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.relations.abc.BaseUniRepositoryTest;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.DocumentContent;
import org.tomfoolery.infrastructures.utils.helpers.mockers.abc.EntityMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.relations.DocumentContentMocker;

import static org.testng.Assert.*;

public abstract class DocumentContentRepositoryTest extends BaseUniRepositoryTest<DocumentContent, DocumentContent.Id, Document.Id> {
    @Override
    protected abstract @NonNull DocumentContentRepository createTestSubject();

    @Override
    protected @NonNull EntityMocker<DocumentContent, DocumentContent.Id> createEntityMocker() {
        return DocumentContentMocker.of();
    }
}