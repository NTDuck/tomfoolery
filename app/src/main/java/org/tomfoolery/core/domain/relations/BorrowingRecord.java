package org.tomfoolery.core.domain.relations;

import lombok.Data;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Instant;

@Data(staticConstructor = "of")
public final class BorrowingRecord implements PatronDocumentRelation {
    private final @NonNull Id id;

    private final @NonNull Instant borrowedTimestamp;
    private @Nullable Instant returnedTimestamp;
}
