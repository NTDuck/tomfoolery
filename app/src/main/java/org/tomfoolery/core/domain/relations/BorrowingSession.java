package org.tomfoolery.core.domain.relations;

import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.utils.contracts.ddd;

import java.time.Instant;

@Value(staticConstructor = "of")
public class BorrowingSession implements ddd.BiRelation<BorrowingSession.Id, Document.Id, Patron.Id> {
    @NonNull Id id;

    @NonNull Instant borrowedTimestamp;
    @NonNull Instant dueTimestamp;

    @Value(staticConstructor = "of")
    public static class Id implements ddd.BiRelationId<Document.Id, Patron.Id> {
        Document.@NonNull Id firstEntityId;
        Patron.@NonNull Id secondEntityId;
    }
}
