package org.tomfoolery.core.domain.relations;

import lombok.Data;
import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.utils.contracts.ddd;

import java.time.Instant;

@Data(staticConstructor = "of")
public final class BorrowingRecord implements ddd.BiRelation<BorrowingRecord.Id, Document.Id, Patron.Id> {
    private final @NonNull Id id;

    private final @NonNull Instant borrowedTimestamp;
    private @Nullable Instant returnedTimestamp;

    @Value(staticConstructor = "of")
    public static class Id implements ddd.BiRelationId<Document.Id, Patron.Id> {
        Document.@NonNull Id firstId;
        Patron.@NonNull Id secondId;
    }
}
