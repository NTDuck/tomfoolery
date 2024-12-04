package org.tomfoolery.core.domain.relations;

import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.utils.contracts.ddd;

@Value(staticConstructor = "of")
public class DocumentContent implements ddd.UniRelation<DocumentContent.Id, Document.Id> {
    @NonNull Id id;

    byte @NonNull [] bytes;

    @Value(staticConstructor = "of")
    public static class Id implements ddd.UniRelationId<Document.Id> {
        Document.@NonNull Id entityId;
    }
}
