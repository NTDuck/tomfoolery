package org.tomfoolery.core.domain.relations;

import lombok.Data;
import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.utils.contracts.ddd;

@Data(staticConstructor = "of")
public final class DocumentContent implements ddd.UniRelation<DocumentContent.Id, Document.Id> {
    private final @NonNull Id id;

    private byte @NonNull [] bytes;

    @Value(staticConstructor = "of")
    public static class Id implements ddd.UniRelationId<Document.Id> {
        Document.@NonNull Id entityId;
    }
}
