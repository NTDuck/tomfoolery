package org.tomfoolery.configurations.contexts;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.documents.FileCachedInMemoryDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.relations.FileCachedInMemoryDocumentContentRepository;

public class FileCachedInMemoryApplicationContext extends InMemoryApplicationContext {
    public static @NonNull FileCachedInMemoryApplicationContext of() {
        return new FileCachedInMemoryApplicationContext();
    }

    @Override
    protected @NonNull DocumentRepository createDocumentRepository() {
        val fileStorageProvider = this.getFileStorageProvider();

        return FileCachedInMemoryDocumentRepository.of(fileStorageProvider);
    }

    @Override
    protected @NonNull DocumentContentRepository createDocumentContentRepository() {
        val fileStorageProvider = this.getFileStorageProvider();
        return FileCachedInMemoryDocumentContentRepository.of(fileStorageProvider);
    }
}
