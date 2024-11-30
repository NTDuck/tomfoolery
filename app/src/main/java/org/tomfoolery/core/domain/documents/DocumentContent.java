package org.tomfoolery.core.domain.documents;

import lombok.Data;
import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.utils.contracts.ddd.ddd;

@Data(staticConstructor = "of")
public final class DocumentContent implements ddd.Entity<DocumentContent.Id> {
    private final @NonNull Id id;

    private byte @NonNull [] bytes;

    @Value(staticConstructor = "of")
    public static class Id implements ddd.EntityId {
        Document.@NonNull Id documentId;
    }
}
