package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.documents;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public class CloudDocumentRepository implements DocumentRepository {
    @Override
    public @NonNull Set<Document> getSavedEntitiesSince(@NonNull Instant fromTimestamp) {
        return Set.of();
    }

    @Override
    public @NonNull Set<Document> getDeletedEntitiesSince(@NonNull Instant fromTimestamp) {
        return Set.of();
    }

    @Override
    public void save(@NonNull Document entity) {

    }

    @Override
    public void delete(Document.@NonNull Id entityId) {

    }

    @Override
    public @Nullable Document getById(Document.@NonNull Id entityId) {
        return null;
    }

    @Override
    public @NonNull List<Document> show() {
        return List.of();
    }
}
