package org.tomfoolery.core.domain.aggregates;

import lombok.Data;
import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.utils.contracts.ddd.ddd;

import java.time.Instant;

@Data(staticConstructor = "of")
public final class BorrowRecord implements ddd.Entity<BorrowRecord.Id> {
    private final @NonNull Id id;

    private final @NonNull Instant borrowedTimestamp;
    private @Nullable Instant returnedTimestamp;

    @Value(staticConstructor = "of")
    public static class Id implements ddd.EntityId {
        Patron.@NonNull Id borrowerId;
        Document.@NonNull Id documentId;
    }
}
